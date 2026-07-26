#!/bin/bash
set -e

cd ~/beacon

# 최신 이미지 pull
docker compose -f docker-compose.prod.yml --env-file .env pull

# 새 이미지로 재기동
docker compose -f docker-compose.prod.yml --env-file .env up -d

# 안 쓰는 예전 이미지 정리 (디스크 용량 관리)
docker image prune -f
