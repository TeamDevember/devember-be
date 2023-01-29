package com.gridians.gridians.domain.user.entity;

import lombok.Getter;

@Getter
public enum CreateType {
    EMAIL("EMAIL"),
    GITHUB("GITHUB"),
    ;

    public String name;
    private CreateType(String name){
        this.name = name;
    }
}
