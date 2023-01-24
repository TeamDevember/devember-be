package com.devember.devember.card.repository;

import com.devember.devember.card.entity.Github;
import com.devember.devember.card.entity.ProfileCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubRepository extends JpaRepository<Github, Long> {

	Optional<Github> findByProfileCard(ProfileCard profileCard);
}
