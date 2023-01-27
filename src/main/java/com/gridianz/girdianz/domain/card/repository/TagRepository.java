package com.gridianz.girdianz.domain.card.repository;

import com.gridianz.girdianz.domain.card.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
