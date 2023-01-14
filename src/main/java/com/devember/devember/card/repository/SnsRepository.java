package com.devember.devember.card.repository;

import com.devember.devember.card.entity.Sns;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SnsRepository extends JpaRepository<Sns, Long> {
	Optional<Sns> findByName(String name);
}
