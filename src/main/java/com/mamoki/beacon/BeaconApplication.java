package com.mamoki.beacon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@EnableScheduling //SSE 하트비트(@Scheduled) 동작에 필요
@SpringBootApplication
public class BeaconApplication {

	public static void main(String[] args) {
		// JVM 기본 타임존을 KST로 고정.
		// 코드 전반이 LocalDateTime.now()를 쓰는데, 이 값은 JVM 기본 타임존을 따라간다.
		// Dockerfile의 TZ 설정이 1차 방어선이고, 이건 인프라 설정이 빠져도 동작하도록 두는 2차 방어선이다.
		// SpringApplication.run() 이전에 실행해야 DB 커넥션·JPA·스케줄러까지 전부 KST로 뜬다.
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));

		SpringApplication.run(BeaconApplication.class, args);
	}

}
