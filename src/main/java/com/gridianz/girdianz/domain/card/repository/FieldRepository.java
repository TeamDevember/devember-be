package com.gridianz.girdianz.domain.card.repository;

import com.gridianz.girdianz.domain.card.entity.Field;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FieldRepository extends JpaRepository<Field, Long> {
	Optional<Field> findByName(String name);
}
