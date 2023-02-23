package com.gridians.gridians.domain.user.repository;

import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByProfileCard_Id(Long id);
    boolean existsByGithub_GithubNumberId(Long id);
    boolean existsByNickname(String nickname);
}