package com.daascmputers.website.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "accessorydto",indexes = {
        @Index(name = "idx_product_name_dto",columnList = "productName"),
        @Index(name = "idx_brand_name_dto",columnList = "brandName"),
        @Index(name = "idx_rating_dto",columnList = "rating"),
        @Index(name = "idx_description_dto",columnList = "description"),
        @Index(name = "idx_image_url_dto",columnList = "imageUrl"),
        @Index(name = "idx_accessory_category_dto",columnList = "accessoryCategory")
})
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AccessoryDTO {
    @Id
    private Integer accessoryId;
    private String productName;
    private String brandName;
    private double rating;
    private String description;
//    private String imageBase64;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private  AccessoryCategory accessoryCategory;


}

