package com.gridians.gridians.domain.user.service;

import com.gridians.gridians.domain.user.entity.User;
import com.gridians.gridians.domain.user.exception.NotFoundImageException;
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
public class PhotoService {

    private final UserRepository userRepository;

    @Value("${custom.path.user-dir}")
    private String path;

    public void updateProfileImage(String userEmail, String base64Image) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(userEmail + " not found"));

        base64Image = base64Image.split(",")[1];
        String filePath = path + "/" + user.getId() + ".jpg";

        byte[] decodeBytes = Base64.getDecoder().decode(base64Image);

        Path savePath = Paths.get(filePath);
        try {
            Files.write(savePath, decodeBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getImage(String path) {
        File file = new File(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        log.info("file path = {}", path);
        try {
            BufferedImage image = ImageIO.read(file);
            ImageIO.write(image, "jpg", bos);
            return bos.toByteArray();
        } catch (IOException e) {
            return getBaseImage();
        }
    }

    public byte[] getProfileImage(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(email + " not found"));

        String filePath = path + "/" + user.getId() + ".jpg";
        return getImage(filePath);
    }

    private byte[] getBaseImage() {
        File file = new File(path + "/baseimage.jpg");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            BufferedImage image = ImageIO.read(file);
            ImageIO.write(image, "jpg", bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new NotFoundImageException("image not found");
        }
    }
}
