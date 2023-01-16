package com.devember.devember.card.dto;


import com.devember.devember.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SkillRequest {

		private User user;
		private String skill;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FieldRequest {

		private User user;
		private String field;

	}
//
//	@Getter
//	@Setter
//	@NoArgsConstructor
//	@AllArgsConstructor
//	public static class Response {
//
//		private String ;
//		private String ;
//		private String ;
//		private String ;
//		private String ;
//		private String ;
//		private String ;
//		private String  ;
//		private String field;
//		private List<String> skillList;
//		private List<String> snsList;
//
//
//	}


}
