package com.gridians.gridians.domain.card.repository;

import com.gridians.gridians.domain.card.entity.Github;
import com.gridians.gridians.domain.card.entity.ProfileCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubRepository extends JpaRepository<Github, Long> {
}
