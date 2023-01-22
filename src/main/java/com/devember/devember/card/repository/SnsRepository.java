package com.devember.devember.card.repository;

import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.card.entity.Sns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;
import java.util.Set;

public interface SnsRepository extends JpaRepository<Sns, Long> {
	Optional<Sns> findByName(String name);
	Set<Sns> findAllByProfileCard(ProfileCard profileCard);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	void deleteAllByProfileCard(ProfileCard pc);



}
