package com.mamoki.beacon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling //SSE 하트비트(@Scheduled) 동작에 필요
@SpringBootApplication
public class BeaconApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeaconApplication.class, args);
	}

}
