package com.forcy.chatapp.media;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class AssetService {

    private static final String UPLOAD_DIR = "uploads/";
    private Logger logger = LoggerFactory.getLogger(AssetService.class);


    public String uploadMedia(Long userId, MultipartFile mediaFile) throws IOException {

        if(!Files.exists(Paths.get(UPLOAD_DIR))){
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        }

        String originalFilename = mediaFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        String mediaId = UUID.randomUUID().toString();
        String filePath = UPLOAD_DIR + mediaId + "." + extension;
        Files.write(Paths.get(filePath), mediaFile.getBytes());
        logger.info("Uploaded media with ID: {} to {}", mediaId, filePath);
        return mediaId + "." + extension;
    }


    public byte[] getMediaFile(String mediaIdWithExtension) throws IOException {
        String filePath = UPLOAD_DIR + mediaIdWithExtension;
        if (!Files.exists(Paths.get(filePath))) {
            throw new IOException("Media file not found: " + filePath);
        }
        return Files.readAllBytes(Paths.get(filePath));
    }
}
