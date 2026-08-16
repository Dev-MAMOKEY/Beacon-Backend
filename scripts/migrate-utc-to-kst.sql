-- ============================================================================
--  UTC → KST 데이터 마이그레이션 (1회성 스크립트)
-- ============================================================================
--
--  [배경]
--  도커 컨테이너 기본 타임존이 UTC라, LocalDateTime.now()가 UTC 벽시계 시각을
--  반환했고 그 값이 DATETIME 컬럼에 그대로 저장되었다.
--  즉 기존 데이터는 전부 실제 한국 시간보다 9시간 이른 값이다.
--
--  [실행 순서 — 반드시 지킬 것]
--    1. 애플리케이션 중지        (docker compose stop app)
--    2. DB 백업                  (아래 STEP 0)
--    3. 이 스크립트 실행         (STEP 1 ~ 4)
--    4. KST 수정본 이미지 배포   (docker compose up -d)
--
--  ⚠️ 순서를 바꾸면 안 된다.
--     KST 배포를 먼저 하면 새로 들어오는 데이터는 KST, 기존 데이터는 UTC가 되어
--     한 테이블에 두 시간대가 섞이고, 이 스크립트가 새 KST 데이터까지
--     9시간 더 밀어버린다.
--
--  ⚠️ 이 스크립트는 절대 두 번 실행하면 안 된다. (18시간이 더해짐)
-- ============================================================================


-- ============================================================================
--  STEP 0. 백업 (SQL이 아니라 셸에서 실행)
-- ============================================================================
--  docker exec beacon-db sh -c 'mysqldump -u root -p"$MYSQL_ROOT_PASSWORD" \
--    --single-transaction --routines --triggers beacon' \
--    > backup_before_kst_$(date +%Y%m%d_%H%M%S).sql
--
--  백업 파일 크기가 0이 아닌지 반드시 확인하고 다음으로 넘어갈 것.
-- ============================================================================


USE beacon;   -- ← 실제 DB 이름이 다르면 수정 (.env의 MYSQL_DATABASE 값)


-- ============================================================================
--  STEP 1. 사전 확인 — 지금 값이 정말 UTC인지 눈으로 검증
-- ============================================================================
--  가장 최근 출석 기록의 시각을 본다.
--  "실제로 출석했던 한국 시간"보다 9시간 이르게 나와야 정상(=마이그레이션 대상)이다.
--  이미 KST로 보인다면 절대 아래를 실행하지 말 것.

SELECT
    attendance_id,
    checked_at                                  AS '현재값(UTC로 추정)',
    DATE_ADD(checked_at, INTERVAL 9 HOUR)       AS '변환후(KST)'
FROM attendance
ORDER BY attendance_id DESC
LIMIT 10;

-- 변환 대상 건수 파악
SELECT 'attendance'   AS 테이블, COUNT(*) AS 건수 FROM attendance
UNION ALL SELECT 'session',      COUNT(*) FROM session
UNION ALL SELECT 'club_member',  COUNT(*) FROM club_member
UNION ALL SELECT 'member',       COUNT(*) FROM member
UNION ALL SELECT 'club',         COUNT(*) FROM club
UNION ALL SELECT 'invite',       COUNT(*) FROM invite
UNION ALL SELECT 'beacon_config',COUNT(*) FROM beacon_config;


-- ============================================================================
--  STEP 2. 변환 실행
-- ============================================================================
--  NULL 값은 DATE_ADD 결과도 NULL이므로 그대로 유지된다. (WHERE 절 불필요)

START TRANSACTION;

-- 출석: 출석시각 + 감사 컬럼
UPDATE attendance SET
    checked_at = DATE_ADD(checked_at, INTERVAL 9 HOUR),
    created_at = DATE_ADD(created_at, INTERVAL 9 HOUR),
    updated_at = DATE_ADD(updated_at, INTERVAL 9 HOUR),
    deleted_at = DATE_ADD(deleted_at, INTERVAL 9 HOUR);

-- 세션: 예정/실제 시작·종료 + 감사 컬럼
UPDATE session SET
    expect_start_at = DATE_ADD(expect_start_at, INTERVAL 9 HOUR),
    expect_end_at   = DATE_ADD(expect_end_at,   INTERVAL 9 HOUR),
    start_at        = DATE_ADD(start_at,        INTERVAL 9 HOUR),
    end_at          = DATE_ADD(end_at,          INTERVAL 9 HOUR),
    created_at      = DATE_ADD(created_at,      INTERVAL 9 HOUR),
    updated_at      = DATE_ADD(updated_at,      INTERVAL 9 HOUR),
    deleted_at      = DATE_ADD(deleted_at,      INTERVAL 9 HOUR);

-- 동아리 가입 정보: 가입일 + 감사 컬럼
UPDATE club_member SET
    joined_at  = DATE_ADD(joined_at,  INTERVAL 9 HOUR),
    created_at = DATE_ADD(created_at, INTERVAL 9 HOUR),
    updated_at = DATE_ADD(updated_at, INTERVAL 9 HOUR),
    deleted_at = DATE_ADD(deleted_at, INTERVAL 9 HOUR);

-- 회원: 리프레시 토큰 발급시각 + 감사 컬럼
UPDATE member SET
    rt_at      = DATE_ADD(rt_at,      INTERVAL 9 HOUR),
    created_at = DATE_ADD(created_at, INTERVAL 9 HOUR),
    updated_at = DATE_ADD(updated_at, INTERVAL 9 HOUR),
    deleted_at = DATE_ADD(deleted_at, INTERVAL 9 HOUR);

-- 동아리
UPDATE club SET
    created_at = DATE_ADD(created_at, INTERVAL 9 HOUR),
    updated_at = DATE_ADD(updated_at, INTERVAL 9 HOUR),
    deleted_at = DATE_ADD(deleted_at, INTERVAL 9 HOUR);

-- 초대코드: 생성/폐기 시각
UPDATE invite SET
    created_at = DATE_ADD(created_at, INTERVAL 9 HOUR),
    revoked_at = DATE_ADD(revoked_at, INTERVAL 9 HOUR);

-- 비콘 설정
UPDATE beacon_config SET
    created_at = DATE_ADD(created_at, INTERVAL 9 HOUR),
    updated_at = DATE_ADD(updated_at, INTERVAL 9 HOUR),
    deleted_at = DATE_ADD(deleted_at, INTERVAL 9 HOUR);


-- ============================================================================
--  STEP 3. 검증 — COMMIT 전에 반드시 확인
-- ============================================================================
--  STEP 1에서 본 것과 같은 행을 다시 조회해서 정확히 9시간 늘었는지 본다.

SELECT
    attendance_id,
    checked_at AS '변환된 값(KST여야 함)'
FROM attendance
ORDER BY attendance_id DESC
LIMIT 10;

-- 세션 시각도 상식적인 시간대(새벽 3시 회의 같은 게 아닌지)인지 확인
SELECT id, session_name, expect_start_at, start_at, end_at
FROM session
ORDER BY id DESC
LIMIT 10;


-- ============================================================================
--  STEP 4. 확정 또는 취소
-- ============================================================================
--  STEP 3 결과가 정상이면 아래 COMMIT 실행:

COMMIT;

--  값이 이상하면 COMMIT 대신 아래를 실행해서 전부 되돌린다:
--  ROLLBACK;


-- ============================================================================
--  마이그레이션 완료 후
-- ============================================================================
--  1. KST 수정본 이미지로 앱 재기동
--  2. docker exec beacon-app date   → KST 및 현재 한국시각 확인
--  3. 신규 출석을 1건 찍어보고, 저장된 checked_at이 실제 시각과 일치하는지 확인
--  4. 대시보드 '오늘' 집계가 정상인지 확인
-- ============================================================================
