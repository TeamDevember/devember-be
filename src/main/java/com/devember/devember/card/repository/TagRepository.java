package com.devember.devember.card.repository;

import com.devember.devember.card.entity.ProfileCardTag;
import com.devember.devember.card.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {

	void deleteAllByProfileCardTag(ProfileCardTag profileCardTag);
}
