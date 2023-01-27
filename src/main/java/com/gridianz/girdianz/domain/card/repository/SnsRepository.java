package com.gridianz.girdianz.domain.card.repository;

import com.gridianz.girdianz.domain.card.entity.ProfileCard;
import com.gridianz.girdianz.domain.card.entity.Sns;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface SnsRepository extends JpaRepository<Sns, Long> {
	Set<Sns> findAllByProfileCard(ProfileCard profileCard);
}
