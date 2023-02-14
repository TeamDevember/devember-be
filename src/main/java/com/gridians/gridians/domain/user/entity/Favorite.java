package com.gridians.gridians.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "favorite")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Favorite {

    @Id @GeneratedValue
    @Column(name = "favorite_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_favorite_user_id")
    private User favoriteUser;

    public void setUser(User user) {
        this.user = user;
    }
}
