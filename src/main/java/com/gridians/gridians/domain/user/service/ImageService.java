package com.gridians.gridians.domain.user.service;

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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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

	@Value("${custom.path.imageBase}")
	private String imageBase;

	@Value("${custom.path.profile}")
	private String profilePath;

	@Value("${custom.path.skill}")
	private String skillPath;

	@Value("${custom.format.profile}")
	private String profileFormat;

	@Value("${custom.format.skill}")
	private String skillFormat;

	@Value("${custom.path.defaultImage}")
	private String defaultImage;

	public void updateProfileImage(String userEmail, String base64Image) {

		User findUser = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new EntityNotFoundException(userEmail + " not found"));

		if(findUser.getProfileImage() != null){
			try {
				Files.delete(Paths.get(findUser.getProfileImage().getPath()));
				profileImageRepository.delete(findUser.getProfileImage());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		//data:image/jpeg;base64,
		String extension = "";
		String type = "image/";
		extension = base64Image.substring(base64Image.indexOf(type) + type.length(), base64Image.indexOf(";base64"));

		if (base64Image.contains(",")) {
			base64Image = base64Image.split(",")[1];
		}

		if (base64Image == null || base64Image.equals("")) {
			throw new NotFoundImageException("ProfileImage not found");
		}

		// 날짜에 맞는 폴더 주소
		Path folder = Paths.get(imageBase, profilePath);

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
		Path filePath = Paths.get(imageBase, profilePath,findUser.getId() + "." + extension);

		try {
			// 파일 생성
			Files.write(filePath, decodeBytes);
			ProfileImage savedImage = profileImageRepository.save(ProfileImage.builder().path(filePath.toString()).user(findUser).build());
			findUser.setProfileImage(savedImage);
			userRepository.save(findUser);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}


	public byte[] getProfileImage(String email) {
		User findUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(email + " not found"));

		if(findUser.getProfileImage() == null){
			return getProfileBaseImage();
		}

		String filePath = findUser.getProfileImage().getPath();
		String extension = filePath.substring(filePath.indexOf(".") + 1);

		Path path = Paths.get(filePath);

		if (Files.exists(path)) {
			Long size = null;
			try {
				size = Files.size(path);
				log.info("file size =" + size + "bytes");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return getProfileImageByteArray(path, extension);
	}

	public byte[] getSkillImage(String skill) {
		Path path = Path.of(imageBase, skillPath, skill.toLowerCase() + "." + skillFormat);
		return getSkillImageByteArray(path);
	}

	private byte[] getProfileImageByteArray(Path path, String extension) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream stream = null;
		log.info("file path = {}", path);
		try {
			stream = new FileInputStream(path.toFile());
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

	private byte[] getSkillImageByteArray(Path path) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		log.info("file path = {}", path);
		try {
			System.out.println(path);
			BufferedImage image = ImageIO.read(path.toFile());
			ImageIO.write(image, skillFormat, bos);
			return bos.toByteArray();
		} catch (IOException e) {
			return getSkillBaseImage();
		}
	}

	private byte[] getProfileBaseImage() {
		Path path = Path.of(imageBase, profilePath, defaultImage);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			BufferedImage image = ImageIO.read(path.toFile());
			ImageIO.write(image, profileFormat, bos);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new NotFoundImageException("Image not found");
		}
	}

	private byte[] getSkillBaseImage() {
		Path path = Path.of(imageBase, skillPath, defaultImage);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try {
			BufferedImage image = ImageIO.read(path.toFile());
			ImageIO.write(image, skillFormat, bos);
			return bos.toByteArray();
		} catch (IOException e) {
			throw new NotFoundImageException("Image not found");
		}
	}

	public void deleteProfileImage(String email) {

		User findUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new EntityNotFoundException(email + " not found"));

		if(findUser.getProfileImage() == null){
			throw new NotFoundImageException("Image not found");
		}

		Path path = Paths.get(findUser.getProfileImage().getPath());

		if (Files.exists(path)) {
			try {
				Files.delete(path);
				profileImageRepository.delete(findUser.getProfileImage());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new NotFoundImageException("ProfileImage not found");
		}
	}
}
