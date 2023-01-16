package com.devember.devember.card.dto;


import com.devember.devember.card.entity.ProfileCard;
import com.devember.devember.card.entity.Skill;
import com.devember.devember.card.entity.Sns;
import com.devember.devember.user.entity.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProfileCardDto {

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CardRequest {

		private User user;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SnsRequest {

		private User user;

		private String sns;
		private String account;

	}

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SnsResponse {

		private String sns;
		private String account;


		public static SnsResponse from(Sns sns) {

			return SnsResponse.builder()
					.sns(sns.getName())
					.account(sns.getAccount())
					.build();
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SkillRequest {

		private User user;
		private String skill;


	}

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SkillResponse {

		private String skill;

		public static SkillResponse from(Skill skill) {

			return SkillResponse.builder()
					.skill(skill.getName())
					.build();
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FieldRequest {

		private User user;
		private String field;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FieldResponse {

		private String field;

	}


	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DetailRequest {
		private User user;

		private String status;
		private String statusMessage;

	}


	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReadResponse {


		private String status;
		private String statusMessage;
		private String field;
		private List<ProfileCardDto.SnsResponse> snsList = new ArrayList<>();
		private List<ProfileCardDto.SkillResponse> skillList = new ArrayList<>();


		public static ReadResponse from(ProfileCard pc) {


			Set<Sns> snsSet = pc.getSnsSet();
			Set<Skill> skillSet = pc.getSkillSet();

			List<SnsResponse> snsList = new ArrayList<>();
			List<SkillResponse> skillList = new ArrayList<>();

			for (Sns sns : snsSet) {
				snsList.add(SnsResponse.from((sns)));
			}

			for (Skill skill : skillSet) {
				skillList.add(SkillResponse.from(skill));
			}


			return ReadResponse.builder()
					.status(pc.getDetail().getStatus())
					.statusMessage(pc.getDetail().getStatusMessage())
					.field(pc.getField().getName())
					.snsList(snsList)
					.skillList(skillList)
					.build();
		}
	}
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DeleteSns {
		private User user;
		private String sns;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DeleteSkill {
		private User user;
		private String skill;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DeleteField {
		private User user;
		private String field;
	}
}
