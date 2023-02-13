package com.gridians.gridians.domain.user.repository;

import com.gridians.gridians.domain.user.entity.Github;
import com.gridians.gridians.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubRepository extends JpaRepository<Github, Long> {
	Optional<Github> findByUser(User user);
}
