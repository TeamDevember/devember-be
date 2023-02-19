package com.gridians.gridians.card.service;

import com.gridians.gridians.domain.card.service.ProfileCardService;
import com.gridians.gridians.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProfileCardServiceTest {

	@Autowired
	ProfileCardService profileCardService;

	@Autowired
	UserRepository userRepository;

//	@Test
//	void create(){
//		User user = userRepository.findById(UUID.fromString("d3b1dba9-9baa-43a5-a60f-48a3b3271d8c"))
//				.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
//
//		List<Sns> snsList = new ArrayList<>();
//
//		Sns sns1 = new Sns();
//		sns1.setName("twitter");
//		sns1.setAccount("utfda");
//
//		Sns sns2 = new Sns();
//		sns2.setName("notion");
//		sns2.setAccount("dlworhd");
//
//		snsList.add(sns1);
//		snsList.add(sns2);
//
//	}

}