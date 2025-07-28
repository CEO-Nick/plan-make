package com.ikhyeon.plan_make.domain.member.vo;

import lombok.Getter;

@Getter
public enum SocialProvider {
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver"),
    GITHUB("github");

    private final String value;

    SocialProvider(String value) {
        this.value = value;
    }
}