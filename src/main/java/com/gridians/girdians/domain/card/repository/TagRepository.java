package com.gridians.girdians.domain.card.repository;

import com.gridians.girdians.domain.card.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
