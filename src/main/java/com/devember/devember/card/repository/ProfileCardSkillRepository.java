package com.devember.devember.card.repository;

import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.card.entity.ProfileCardSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface ProfileCardSkillRepository extends JpaRepository<ProfileCardSkill, Long> {

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	void deleteAllByProfileCard(ProfileCard profileCard);
}
