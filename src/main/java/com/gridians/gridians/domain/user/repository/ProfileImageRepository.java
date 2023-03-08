package com.gridians.gridians.domain.user.repository;

import com.gridians.gridians.domain.user.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
}
