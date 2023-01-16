package com.devember.devember.card.repository;

import com.devember.devember.card.entity.Detail;
import com.devember.devember.card.entity.ProfileCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DetailRepository extends JpaRepository<Detail, Long> {

	Optional<Detail> findByProfileCard(ProfileCard pc);
}
