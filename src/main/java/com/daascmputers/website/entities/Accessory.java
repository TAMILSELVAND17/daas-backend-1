package com.daascmputers.website.entities;

import com.daascmputers.website.dto.AccessoryCategory;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.URL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Valid
@Table(name = "accessory",indexes = {
        @Index(name = "idx_product_name",columnList = "productName"),
        @Index(name = "idx_brand_name", columnList = "brandName"),
        @Index(name = "idx_rating",columnList = "rating"),
        @Index(name = "idx_accessory_category",columnList = "accessoryCategory"),
        @Index(name = "idx_image_url",columnList = "imageUrl")
})
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Accessory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accessoryId;

    @NotBlank(message = "name not to blank")
    private String productName;

    @NotBlank(message = "brnad not to be blank")
    private String brandName;


    @NotNull(message = "Not to be Null")
    private double rating;

    @NotBlank(message = "Not to be blank")
    private String description;


//    private String imageType;
//
//    private String imageName;
//
//    @Lob
//    private byte[] imageData;


    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Category must be provided")
    private AccessoryCategory accessoryCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
