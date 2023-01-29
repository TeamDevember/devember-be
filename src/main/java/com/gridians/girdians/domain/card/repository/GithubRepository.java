package com.gridians.girdians.domain.card.repository;

import com.gridians.girdians.domain.card.entity.Github;
import com.gridians.girdians.domain.card.entity.ProfileCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubRepository extends JpaRepository<Github, Long> {

	Optional<Github> findByProfileCard(ProfileCard profileCard);
}
