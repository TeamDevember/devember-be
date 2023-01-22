package com.devember.devember.card.repository;

import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.card.entity.ProfileCardTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ProfileCardTagRepository extends JpaRepository<ProfileCardTag, Long> {


	Set<ProfileCardTag> findAllByProfileCard(ProfileCard profileCard);
}
