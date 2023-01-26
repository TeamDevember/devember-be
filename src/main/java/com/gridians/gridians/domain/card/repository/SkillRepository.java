package com.gridians.gridians.domain.card.repository;

import com.gridians.gridians.domain.card.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
	Optional<Skill> findByName(String name);
}
