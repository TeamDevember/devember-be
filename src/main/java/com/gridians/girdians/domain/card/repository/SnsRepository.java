package com.gridians.girdians.domain.card.repository;

import com.gridians.girdians.domain.card.entity.ProfileCard;
import com.gridians.girdians.domain.card.entity.Sns;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface SnsRepository extends JpaRepository<Sns, Long> {
	Set<Sns> findAllByProfileCard(ProfileCard profileCard);
}
