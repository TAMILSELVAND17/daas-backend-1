package com.daascmputers.website.service;

import com.daascmputers.website.dto.AccessoryDTO;
import com.daascmputers.website.entities.Accessory;
import com.daascmputers.website.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AccessoryService {
    CompletableFuture<Accessory> store(Accessory accessory, MultipartFile image);
    CompletableFuture<Accessory> clear(int accessoryId);
    CompletableFuture<List<AccessoryDTO>> getAllAccessories();
//    CompletableFuture<User> addAccessoriesForUser(int userId, List<Accessory> accessories, List<MultipartFile> images);
//    byte[] getImage(int id);
    CompletableFuture<User> addAccessoriesForUser(int userId, List<Accessory> accessories);
}
