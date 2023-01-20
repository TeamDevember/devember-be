package com.devember.devember.card.repository;

import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.card.entity.Sns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface SnsRepository extends JpaRepository<Sns, Long> {
	Optional<Sns> findByName(String name);
	List<Sns> findAllByProfileCard(ProfileCard profileCard);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	void deleteAllByProfileCard(ProfileCard pc);
}
