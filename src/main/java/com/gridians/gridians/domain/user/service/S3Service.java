package com.gridians.gridians.domain.user.service;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
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

	@Value("${custom.gridians-s3.path}")
	private String path;
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

	public String getProfileImage(String id) {

		String defaultProfileImage = "default.png";

		try {
			amazonS3.getObject(bucket, id);
			return amazonS3.getUrl(bucket, id).toString();
		} catch (AmazonS3Exception exception){
			return amazonS3.getUrl(bucket, defaultProfileImage).toString();
		}
	}

	public String getSkillImage(String skill) {
		return amazonS3.getUrl(bucket, skill).toString();
	}
}
