package com.gridians.gridians.domain.user.dto;

import lombok.*;

@Data
public class FavoriteDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private Long profileCardId;
    }
}
