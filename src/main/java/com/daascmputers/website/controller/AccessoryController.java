package com.daascmputers.website.controller;


import com.daascmputers.website.dto.AccessoryDTO;
import com.daascmputers.website.entities.Accessory;
import com.daascmputers.website.entities.User;
import com.daascmputers.website.service.AccessoryService;
import com.daascmputers.website.utility.ErrorStructure;
import com.daascmputers.website.utility.ResponseStructure;
import com.daascmputers.website.utility.RestResponseBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("api/product")
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin
@AllArgsConstructor
public class AccessoryController {
    @Autowired
    private AccessoryService accessoryService;
    @Autowired
    private RestResponseBuilder restResponseBuilder;


    @CacheEvict(value = "accessories", allEntries = true)
    @PostMapping(value = "/store", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<ResponseEntity<ResponseStructure<Accessory>>> store(@Valid @RequestPart Accessory accessory, @RequestPart MultipartFile image) {
        if (image.isEmpty()) {
            return CompletableFuture.completedFuture(
                    (ResponseEntity<ResponseStructure<Accessory>>) (ResponseEntity<?>) restResponseBuilder.error(HttpStatus.BAD_REQUEST, "Image file is required", null));
        }

        CompletableFuture<Accessory> future = accessoryService.store(accessory, image);
        return future.thenApply(saved -> restResponseBuilder.success(HttpStatus.CREATED, "successfully store the acessory", saved))
                .exceptionally(exception -> {
                    ResponseStructure errorResponse = new ResponseStructure<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "Failed to fetch accessories: " + exception.getMessage(),
                            null
                    );
                    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }



    @DeleteMapping("/{accessoryId}")
    @CacheEvict(value = "accessories", allEntries = true)
    public CompletableFuture<ResponseEntity<ResponseStructure<Accessory>>> clear(@PathVariable int accessoryId) {

        CompletableFuture<Accessory> future = accessoryService.clear(accessoryId);
        return future.thenApply(dwleted -> restResponseBuilder.success(HttpStatus.OK, "Accessory will be deleted: " + dwleted.getBrandName(), dwleted))
                .exceptionally(exception -> {
                    ResponseStructure errorResponse = new ResponseStructure<>(
                            HttpStatus.NOT_FOUND.value(),
                            "Failed to fetch accessories: " + exception.getMessage(),
                            null
                    );
                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
                });
    }



    @Cacheable(value = "accessories")
    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<ResponseStructure<List<AccessoryDTO>>>> getAllAccessories() {

//        List<AccessoryDTO> list = accessoryService.getAllAccessories();
//        return restResponseBuilder.success(HttpStatus.OK, "all accessories Fetched", list);

        return accessoryService.getAllAccessories()
                .thenApply(accessories->restResponseBuilder.success(HttpStatus.OK,"Fetch all accessories 😜",accessories));
    }



    @CacheEvict(value = "accessories", allEntries = true)
    @PostMapping(path = "/user/{userId}/add-accessories",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompletableFuture<ResponseEntity<ResponseStructure<User>>> addAccessoriesToUser(
            @PathVariable int userId,
            @RequestPart("accessories") String accessoryJSON) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Accessory> accessories = objectMapper.readValue(
                    accessoryJSON, new TypeReference<List<Accessory>>() {}
            );

            return accessoryService.addAccessoriesForUser(userId, accessories)
                    .thenApply(user -> restResponseBuilder.success(
                            HttpStatus.OK, "User Interest Sent To Client", user));

        } catch (Exception ex) {
            ResponseStructure<User> errorResponse = new ResponseStructure<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Failed to add accessories: " + ex.getMessage(),
                    null
            );
            return CompletableFuture.completedFuture(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
        }
    }



//    @GetMapping("/image/{id}")
//    public ResponseEntity<ResponseStructure<byte[]>> getImage(@PathVariable int id) {
//       byte[] data=accessoryService.getImage(id);
//return restResponseBuilder.success(HttpStatus.OK,"Image send suucessfully",data);
//
//    }

//    @CacheEvict(value = "accessories", allEntries = true)
//    @PostMapping(value = "/user/{userId}/add-accessories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public CompletableFuture<ResponseEntity<ResponseStructure<User>>> addAccessoriesToUser(
//            @PathVariable int userId,
//            @RequestPart("accessories") MultipartFile accessoryJsonFile,
//            @RequestPart("images") List<MultipartFile> images) {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            List<Accessory> accessories = mapper.readValue(
//                    accessoryJsonFile.getInputStream(),
//                    new TypeReference<List<Accessory>>() {
//                    }
//            );
//
//            CompletableFuture<User> future = accessoryService.addAccessoriesForUser(userId, accessories, images);
//            return future.thenApply(user -> restResponseBuilder.success(
//                    HttpStatus.OK, "User Interest Sent To Client", user))
//                    .exceptionally(exception -> {
//                        ResponseStructure<User> errorResponse = new ResponseStructure<>(
//                                HttpStatus.BAD_REQUEST.value(),
//                                "FFailed to add accessories: " + exception.getMessage(),
//                                null
//                        );
//                        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//                    });
//        } catch (Exception e) {
//            return CompletableFuture.completedFuture(
//                    (ResponseEntity<ResponseStructure<User>>) (ResponseEntity<?>)
//                            restResponseBuilder.error(HttpStatus.BAD_REQUEST, "Error parsing accessories JSON: " + e.getMessage(), e));
//        }
//    }
}