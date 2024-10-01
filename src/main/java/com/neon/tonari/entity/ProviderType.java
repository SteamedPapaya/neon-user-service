package com.neon.tonari.entity;

/**
 * 소셜 로그인 제공자 유형을 나타내는 열거형입니다.
 */
public enum ProviderType {

    LOCAL,
    GOOGLE,
    KAKAO,
    NAVER
    ;

    public static ProviderType of(String type) {
        return switch (type) {
            case "local" -> ProviderType.LOCAL;
            case "google" -> ProviderType.GOOGLE;
            case "kakao" -> ProviderType.KAKAO;
            case "naver" -> ProviderType.NAVER;
            default -> null;
        };
    }


}