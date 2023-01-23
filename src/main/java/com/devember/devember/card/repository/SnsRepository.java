package com.devember.devember.card.repository;

import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.card.entity.Sns;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface SnsRepository extends JpaRepository<Sns, Long> {
	Set<Sns> findAllByProfileCard(ProfileCard profileCard);
}
