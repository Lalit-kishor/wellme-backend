package com.ultimate.wellme.services;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ultimate.wellme.models.CloudinaryUploadResult;

@Service
public class CloudinaryService {

        private final Cloudinary cloudinary;
    
        public CloudinaryService(@Value("${cloudinary.cloud-name}") String cloudName,
                                                 @Value("${cloudinary.api-key}") String apiKey,
                                                 @Value("${cloudinary.api-secret}") String apiSecret) {
                
                                                    this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                                                        "cloud_name", cloudName,
                                                        "api_key", apiKey,
                                                        "api_secret", apiSecret
                                                    ));
        }

        // Uploads an image to Cloudinary and returns the upload result

        @SuppressWarnings("unchecked")
        public CloudinaryUploadResult uploadImage(MultipartFile file) {
            
            if(file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            if(!isValidImageFile(file)) {
                throw new IllegalArgumentException("Only image files are allowed");
            }

            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "wellme/profiles",
                    "width", 400,
                    "height", 400,
                    "crop", "fill",
                    "quality", "auto",
                    "format", "jpg"
            );

            Map<String, Object> result = null;

            try {
                result = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            } catch (Exception e) {
                System.err.println("Cloudinary upload failed: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to upload image to Cloudinary", e);
            }
             
            if (result == null) {
                throw new RuntimeException("Cloudinary upload returned null result");
            }

            CloudinaryUploadResult uploadResult = new CloudinaryUploadResult(
                (String) result.get("secure_url"),
                (String) result.get("public_id"), 
                (String) result.get("format"), 
                (Integer) result.get("bytes"), 
                (Integer) result.get("height"), 
                (Integer) result.get("width"), 
                (String) result.get("created_at")
            );

           return uploadResult;
        }

        public boolean isValidImageFile(MultipartFile file) {
            String contentType = file.getContentType();

            return contentType!=null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp")    
            );
        }

        // Deletes an image from Cloudinary using public_id

        @SuppressWarnings("unchecked")
        public Map<String, Object> deleteImage(String publicId) throws IOException {

            if(publicId==null || publicId.trim().isEmpty()) {
                throw new IllegalArgumentException("Public ID can not be null or empty");
            }

            return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
}
