package com.devember.devember.card.entity;

import com.devember.global.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Detail extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long followers;
	private Long following;

	private String status;
	@Column(name = "status_message")
	private String statusMessage;

	private String location;

	@Column(name = "committed_at")
	private LocalDate committedAt;


}
