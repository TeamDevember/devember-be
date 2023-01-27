package com.gridianz.girdianz.domain.card.repository;

import com.gridianz.girdianz.domain.card.entity.ProfileCard;
import com.gridianz.girdianz.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileCardRepository extends JpaRepository<ProfileCard, Long> {

	Optional<ProfileCard> findByUser(User user);

}
