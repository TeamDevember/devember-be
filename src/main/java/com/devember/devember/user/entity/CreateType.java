package com.devember.devember.user.entity;

import lombok.Getter;

@Getter
public enum CreateType {
    EMAIL("EMAIL"),
    GOOGLE("GOOGLE"),
    GITHUB("GITHUB"),
    KAKAO("KAKAO")
    ;

    public String name;
    private CreateType(String name){
        this.name = name;
    }
}
