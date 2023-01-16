package com.devember.devember.card.entity;


import com.devember.devember.entity.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Github extends BaseEntity {

	@Id
	private Long id;

	private String name;
	private String Login;

	@Column(name = "profile_image_url")
	private String profileImageUrl;

	private String url;

	private String recentCommitAt;
	private String recentCommitMessage;

	@Column(name = "followers_url")
	private Long followersUrl;

	@Column(name = "following_url")
	private Long followingUrl;

	private String location;

	@Column(name = "created_at")
	@CreatedDate
	private LocalDateTime createdAt;

	@Column(name = "modified_at")
	@LastModifiedDate
	private LocalDateTime modifiedAt;

}
