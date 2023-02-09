package com.gridians.gridians.domain.user.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.domain.user.type.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class S3Service {

	private final UserRepository userRepository;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;

	public void upload(String email, MultipartFile multipartFile) throws IOException {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
		String fileName = user.getId().toString();

		ObjectMetadata objMeta = new ObjectMetadata();

		objMeta.setContentType(multipartFile.getContentType());
		objMeta.setContentLength(multipartFile.getInputStream().available());
		amazonS3.putObject(bucket, fileName, multipartFile.getInputStream(), objMeta);
	}

	public String getUrl(String id){
		return amazonS3.getUrl(bucket, id).toString();
	}
}
