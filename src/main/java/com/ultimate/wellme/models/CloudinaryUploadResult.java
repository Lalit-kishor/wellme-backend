package com.ultimate.wellme.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CloudinaryUploadResult {
    
    private String imageUrl;
    
    @Id
    private String publicId;
    
    private String format;
    private Integer fileSize;
    private Integer height;
    private Integer width;
    private String createdAt;

}
