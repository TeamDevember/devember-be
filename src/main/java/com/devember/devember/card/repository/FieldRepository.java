package com.devember.devember.card.repository;

import com.devember.devember.card.entity.Field;
import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FieldRepository extends JpaRepository<Field, Long> {

	Optional<Field> findByProfileCard(ProfileCard pc);
	Optional<Field> findByName(String name);
	Optional<Field> findByProfileCard_User(User user);
}
