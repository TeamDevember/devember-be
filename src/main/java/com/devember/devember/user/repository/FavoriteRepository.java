package com.devember.devember.user.repository;

import com.devember.devember.user.entity.Favorite;
import com.devember.devember.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUser(User user);
}
