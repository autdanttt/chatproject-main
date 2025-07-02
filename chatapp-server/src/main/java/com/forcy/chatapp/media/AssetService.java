package com.forcy.chatapp.media;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final Cloudinary cloudinary;

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
    public String uploadToCloudinary(MultipartFile file, String folder) {
        try {
            String mediaId = UUID.randomUUID().toString();
            String publicId = folder + "/" + mediaId;

            Map<String, Object> options = Map.of(
                    "public_id", publicId,
                    "overwrite", true
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Upload failed", e);
        }
    }

//    public void deleteFromCloudinary(String mediaId) {
//        try {
//            Map result = cloudinary.uploader().destroy(mediaId, ObjectUtils.emptyMap());
//            System.out.println("Delete result: " + result);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to delete file from Cloudinary", e);
//        }
//    }
    public void deleteAvatarIfNotDefault(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.contains("default.jpg")) {
            return; // Ảnh mặc định, không cần xóa
        }

        try {
            String publicId = extractPublicIdFromUrl(avatarUrl); // ✅ Trích public_id
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            e.printStackTrace(); // log nếu cần
        }
    }

    public String extractPublicIdFromUrl(String url) {
        // Trích phần sau `/upload/` và trước phần mở rộng `.jpg`
        String[] parts = url.split("/upload/");
        if (parts.length < 2) throw new IllegalArgumentException("Invalid Cloudinary URL");

        String path = parts[1]; // "v1751298135/avatar/abc.jpg"
        int dotIndex = path.lastIndexOf('.');
        String noExt = (dotIndex != -1) ? path.substring(0, dotIndex) : path;

        // Bỏ version (v1751298135/)
        int slashIndex = noExt.indexOf('/');
        return (slashIndex != -1) ? noExt.substring(slashIndex + 1) : noExt;
    }
}
