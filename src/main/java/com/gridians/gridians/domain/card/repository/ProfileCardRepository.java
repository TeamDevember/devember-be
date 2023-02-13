package com.gridians.gridians.domain.card.repository;

import com.gridians.gridians.domain.card.entity.ProfileCard;
import com.gridians.gridians.domain.user.entity.Favorite;
import com.gridians.gridians.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface ProfileCardRepository extends JpaRepository<ProfileCard, Long> {
	Optional<ProfileCard> findByUser(User user);

	Page<ProfileCard> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
