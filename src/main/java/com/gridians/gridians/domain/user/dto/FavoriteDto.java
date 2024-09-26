package com.gridians.gridians.domain.user.dto;

import lombok.*;

@Data
public class FavoriteDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private Long profileCardId;
    }
}
