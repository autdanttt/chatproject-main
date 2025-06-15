package com.forcy.chatapp.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/media")
public class MediaController {

    @Autowired
    private AssetService assetService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMedia(@RequestParam Long userId, @RequestParam("mediaFile")MultipartFile mediaFile) throws IOException {
        String mediaId = assetService.uploadMedia(userId, mediaFile);
        Map<String, String> response = new HashMap<>();
        response.put("mediaId", mediaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getMediaFile(@RequestParam String mediaId) throws IOException {
        byte[] fileContent = assetService.getMediaFile(mediaId);
        String extension = mediaId.substring(mediaId.lastIndexOf(".") + 1).toLowerCase();
        MediaType contentType = getMediaType(extension);
        return ResponseEntity.ok().contentType(contentType).body(fileContent);
    }

    private MediaType getMediaType(String extension) {
        switch (extension) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
