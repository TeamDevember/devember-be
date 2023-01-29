package com.gridians.girdians.domain.user.repository;

import com.gridians.girdians.domain.user.entity.User;
import com.gridians.girdians.domain.user.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUser(User user);
}
