package com.devember.devember.card.repository;

import com.devember.devember.card.entity.Github;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubRepository extends JpaRepository<Github, Long> {
}
