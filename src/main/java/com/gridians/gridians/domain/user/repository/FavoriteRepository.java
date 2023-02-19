package com.gridians.gridians.domain.user.repository;

import com.gridians.gridians.domain.user.entity.Favorite;
import com.gridians.gridians.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Page<Favorite> findAllByUser(User user, Pageable pageable);
    Optional<Favorite> findByUserAndFavoriteUser(User user, User favoriteUser);
}
