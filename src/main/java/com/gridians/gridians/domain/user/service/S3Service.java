package com.gridians.gridians.domain.user.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.UserException;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.global.config.aws.S3ObjectClosable;
import com.gridians.gridians.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
@Slf4j
public class S3Service {

	@Value("${custom.gridians-s3.path}")
	private String path;

	private final UserRepository userRepository;

	@Value("${custom.gridians-s3.default-profile-image}")
	private String defaultProfileImage;

	@Value("${custom.gridians-s3.default-skill-image}")
	private String defaultSkillImage;

	@Value("${custom.gridians-s3.skill.extension}")
	private String extension;


	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;

	public void upload(String email, MultipartFile multipartFile) throws IOException {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND));
		String fileName = user.getId().toString();
		ObjectMetadata objMeta = new ObjectMetadata();
		
		String originalFilename = multipartFile.getOriginalFilename().toLowerCase();

		if (originalFilename != null) {
			if (originalFilename.endsWith(".jpg") || originalFilename.endsWith(".png")) {
				objMeta.setContentType(multipartFile.getContentType());
			} else {
				throw new UserException(ErrorCode.UPROAD_ONLY_IMAGE_FILE);
			}
		}
		objMeta.setContentLength(multipartFile.getInputStream().available());
		amazonS3.putObject(bucket, fileName, multipartFile.getInputStream(), objMeta);
	}

	public String getProfileImage(String id) throws IOException {

		try(final var s3ObjectClosable = new S3ObjectClosable(amazonS3.getObject(bucket, id))) {
			return amazonS3.getUrl(bucket, id).toString();
		} catch (AmazonS3Exception exception) {
			return amazonS3.getUrl(bucket, defaultProfileImage).toString();
		}
	}

	public String getSkillImage(String skill) throws IOException {

		try(final var s3ObjectClosable = new S3ObjectClosable(amazonS3.getObject(bucket, skill))) {
			return amazonS3.getUrl(bucket, skill).toString();
		} catch (AmazonS3Exception exception) {
			return amazonS3.getUrl(bucket, defaultSkillImage).toString();
		}
	}
}
