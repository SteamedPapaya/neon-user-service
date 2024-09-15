package com.neon.tonari;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 메인 애플리케이션 클래스입니다.
 * 사용자 및 인증 마이크로서비스의 시작점을 제공합니다.
 */
@SpringBootApplication
public class TonariApplication {

	/**
	 * 애플리케이션의 진입점입니다.
	 *
	 * @param args 프로그램 인자
	 */
	public static void main(String[] args) {
		SpringApplication.run(TonariApplication.class, args);
	}

}
