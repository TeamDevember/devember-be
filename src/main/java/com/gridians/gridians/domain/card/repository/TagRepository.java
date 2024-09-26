package com.gridians.gridians.domain.card.repository;

import com.gridians.gridians.domain.card.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
