package com.gridians.gridians.domain.user.service;

import com.gridians.gridians.domain.card.repository.SkillRepository;
import com.gridians.gridians.domain.user.entity.ProfileImage;
import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.NotFoundImageException;
import com.gridians.gridians.domain.user.repository.ProfileImageRepository;
import com.gridians.gridians.domain.user.repository.UserRepository;
import com.gridians.gridians.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

	private final ProfileImageRepository profileImageRepository;

//1. 유저가 이미지를 저장하기 전에 조회 -> DB 체크 해보고 저장된 DB가 없으면 기본 이미지 반환
//2. 유저가 최초 이미지를 저장, 수정했을 때 -> DB 체크 해보고 저장된 DB가 없으면 오늘 날짜의 디렉토리를 생성해서 저장 후 DB에 반영, 저장된 DB가 있으면 그 날짜에 해당하는 디렉토리에 가서 파일 확인 후에 새로운 사진 등록하고 마지막에 삭제
//3. 유저가 이미지를 삭제할 때 해당 DB 체크 후에 날짜에 맞는 디렉토리로 이동해서 삭제 DB에서도 삭제

	private final UserRepository userRepository;

	@Value("${custom.path.profileImages}")
	private String profileImagePath;

	@Value("${custom.path.skillImages}")
	private String skillImagePath;

	@Value("${custom.path.defaultImage}")
	private String defaultImage;

	public void updateProfileImage(String userEmail, String base64Image) {

		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new EntityNotFoundException(userEmail + " not found"));

		if(user.getProfileImage() != null){
			try {
				Files.delete(Paths.get(user.getProfileImage().getPath()));
				profileImageRepository.delete(user.getProfileImage());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		//data:image/jpeg;base64,
		String extension = "";
		String imageType = "image/";
		extension = base64Image.substring(base64Image.indexOf(imageType) + imageType.length(), base64Image.indexOf(";base64"));

		if (base64Image.contains(",")) {
			base64Image = base64Image.split(",")[1];
		}

		if (base64Image == null || base64Image.equals("")) {
			throw new NotFoundImageException("ProfileImage not found");
		}

		// 날짜에 맞는 폴더 주소
		Path folder = Paths.get(profileImagePath);

		// 위의 폴더 주소가 이미 존재하는지 여부 후에 없으면 폴더 생성
		if (!Files.isDirectory(folder)) {
			try {
				Files.createDirectory(folder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// base64를 byte[]로 디코딩 (Output에 이미지로 쓰기 위함)
		byte[] decodeBytes = Base64.getDecoder().decode(base64Image);

		// 파일 주소 생성
		Path filePath = Paths.get(profileImagePath + "/" + user.getId() + "." + extension);

		try {
			// 파일 생성
			Files.write(filePath, decodeBytes);
			ProfileImage savedImage = profileImageRepository.save(ProfileImage.builder().path(filePath.toString()).user(user).build());
			user.setProfileImage(savedImage);
			userRepository.save(user);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}


	public byte[] getProfileImage(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(email + " not found"));

		String filePath = user.getProfileImage().getPath();
		String extension = filePath.substring(filePath.indexOf(".") + 1);
		System.out.println(extension);

		Path path = Paths.get(filePath);

		if (Files.exists(path)) {
			Long size = null;
			try {
				size = Files.size(path);
				String sizeString = Long.toString(size) + "bytes";
				log.info("file size =" + sizeString);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return getProfileImageByteArray(filePath, extension);
	}

	public byte[] getSkillImage(String skill) {

		String filePath = skillImagePath + "/" + skill.toLowerCase() + ".png";
		return getSkillImageByteArray(filePath);
	}

	private byte[] getProfileImageByteArray(String path, String extension) {
		File file = new File(path);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream stream = null;
		log.info("file path = {}", path);
		try {
			stream = new FileInputStream(file);
			BufferedImage image = ImageIO.read(stream);
			ImageIO.write(image, extension, bos);

			return bos.toByteArray();
		} catch (IOException e) {
			return getProfileBaseImage();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					log.error("ERROR closing image input stream: " + e.getMessage(), e);
				}
			}
		}
	}

	private byte[] getSkillImageByteArray(String path) {
		File file = new File(path);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		log.info("file path = {}", path);
		try {
			BufferedImage image = ImageIO.read(file);
			ImageIO.write(image, "png", bos);
			return bos.toByteArray();
		} catch (IOException e) {
			return getSkillBaseImage();
		}
	}

	private byte[] getProfileBaseImage() {
		File file = new File(profileImagePath + "/" + defaultImage);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			BufferedImage image = ImageIO.read(file);
			ImageIO.write(image, "png", bos);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new NotFoundImageException("image not found");
		}
	}

	private byte[] getSkillBaseImage() {
		File file = new File(skillImagePath + "/" + defaultImage);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			BufferedImage image = ImageIO.read(file);
			ImageIO.write(image, "png", bos);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new NotFoundImageException("image not found");
		}
	}

	public void deleteProfileImage(String email) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(email + " not found"));

		if(user.getProfileImage() == null){
			throw new NotFoundImageException("Image not found");
		}

		Path path = Paths.get(user.getProfileImage().getPath());

		if (Files.exists(path)) {
			try {
				Files.delete(path);
				profileImageRepository.delete(user.getProfileImage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new NotFoundImageException("ProfileImage not found");
		}
	}
}
