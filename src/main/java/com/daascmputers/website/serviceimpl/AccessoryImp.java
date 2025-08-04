package com.daascmputers.website.serviceimpl;

import com.daascmputers.website.dto.AccessoryDTO;
import com.daascmputers.website.entities.Accessory;
import com.daascmputers.website.entities.User;
import com.daascmputers.website.exceptionhandler.Exceptions;
import com.daascmputers.website.repository.AccessoryDTORepository;
import com.daascmputers.website.repository.AccessoryRepository;
import com.daascmputers.website.repository.UserRepository;
import com.daascmputers.website.service.AccessoryService;
import com.daascmputers.website.tools.CloudinaryHelper;
import com.daascmputers.website.tools.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@Service
@AllArgsConstructor
@Slf4j
public class AccessoryImp implements AccessoryService {
    @Autowired
    private CloudinaryHelper cloudinaryHelper;

    @Autowired
    private AccessoryRepository accessoryRepository;

    @Qualifier("taskExecutor")
    private Executor executor;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private static final String IMAGE_BASE_URL = "http://localhost:8081/api/product/image/";



    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

    @Autowired
    private AccessoryDTORepository accessoryDTORepository;

    @Override
    @Transactional
    @Async("virtualThreadExecutor")
    public CompletableFuture<Accessory> store(Accessory accessory, MultipartFile image) {
//        try {
//            accessory.setImageData(image.getBytes());
//            accessory.setImageName(image.getOriginalFilename());
//            accessory.setImageType(image.getContentType());
//
//            Accessory saved = accessoryRepository.save(accessory);
//            AccessoryDTO dto = toDTO(saved);
//
//            accessoryDTORepository.save(dto);
//
//            return CompletableFuture.completedFuture(saved);
//        } catch (IOException e) {
//            throw new RuntimeException("Error while setting the image: " + e.getMessage());
//        }
        try {
           String imageUrl= cloudinaryHelper.uploadImage(image);
           accessory.setImageUrl(imageUrl);

            Accessory saved = accessoryRepository.save(accessory);
            AccessoryDTO dto = toDTO(saved);

            accessoryDTORepository.save(dto);

            return CompletableFuture.completedFuture(saved);
        } catch (IOException e) {
            throw new RuntimeException("Error while setting the image: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public CompletableFuture<Accessory> clear(int accessoryId) {
        Optional<Accessory> optional = accessoryRepository.findById(accessoryId);
        if (optional.isPresent()) {
            Accessory deleted = optional.get();
            accessoryRepository.delete(deleted);
            accessoryDTORepository.deleteById(accessoryId);
            return CompletableFuture.completedFuture(deleted);
        } else {
            throw new NoSuchElementException("Accessory with ID " + accessoryId + " not found");
        }
    }

//    @Override
//    @Transactional(readOnly = false)
//    @Async
//    public CompletableFuture<User> addAccessoriesForUser(int userId, List<Accessory> accessories, List<MultipartFile> images) {
//               User user=userRepository.findById(userId).orElseThrow(()-> new Exceptions("User not found when Add To Cart"));
//               List<Accessory> saved=new ArrayList<>();
//               for(int i=0;i<accessories.size();i++){
//                   Accessory ac=accessories.get(i);
////                   MultipartFile im= images.get(i);
//try{
////                   ac.setImageType(im.getContentType());
////                   ac.setImageData(im.getBytes());
////                   ac.setImageName(im.getOriginalFilename());
//                   saved.add(ac);
//                  } catch (Exception e) {
//                       throw new Exceptions("Exception occured storing User Accessories"+e);
//                    }
//               }
//               user.getAccessories().addAll(saved);
//               userRepository.save(user);
//
//        StringBuilder message = new StringBuilder("The following accessories were added:\n\n");
//        for (Accessory ac : saved) {
//            message.append("• Product: ").append(ac.getProductName())
//                    .append(", Brand: ").append(ac.getBrandName()).append("\n");
//        }
//        message.append("\nTotal Added: ").append(saved.size());
//        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
//            message.append("User Email: ").append(user.getEmail()).append("\n");
//        } else if (user.getMobile() != null && !user.getMobile().isEmpty()) {
//            message.append("User Mobile: ").append(user.getMobile()).append("\n");
//        } else {
//            message.append("No contact information available for user.\n");
//        }
//        emailService.sendEmail(message.toString());
//
//        user.getAccessories().clear();
////        userRepository.save(user);
//
//        return CompletableFuture.completedFuture(user);
//    }

@Override
@Transactional
@Async
public CompletableFuture<User> addAccessoriesForUser(int userId, List<Accessory> accessories) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new Exceptions("User not found when adding accessories"));

    user.getAccessories().addAll(accessories);
    User saved=userRepository.save(user);

    CompletableFuture.runAsync(()->{
        // Build email message
        StringBuilder message = new StringBuilder("The following accessories were added:\n\n");
        for (Accessory ac : accessories) {
            message.append("• Product: ").append(ac.getProductName())
                    .append(", Brand: ").append(ac.getBrandName()).append("\n");
        }

        message.append("\nTotal Added: ").append(accessories.size());
        if (user.getEmail() != null)
            message.append("User Email: ").append(user.getEmail()).append("\n");
        else if (user.getMobile() != null)
            message.append("User Mobile: ").append(user.getMobile()).append("\n");
        else
            message.append("No contact information available.\n");

        emailService.sendEmail(message.toString());

        user.getAccessories().clear();
        userRepository.save(user);
    });

    return CompletableFuture.completedFuture(saved);
}

    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<List<AccessoryDTO>> getAllAccessories() {
    return CompletableFuture.supplyAsync(()->{
   log.info("✅ Executing getAllAccessories on thread: {}",Thread.currentThread());
   return accessoryDTORepository.findAllProjectedBy();
});
//        try {    return virtualThreadExecutor.submit(() -> {
//            log.info("🔧 Executing getAllAccessories on thread: {}", Thread.currentThread());
//            return accessoryDTORepository.findAllProjectedBy();
//        }).get();
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException("Error fetching accessories with blockingExecutor", e);
//        }

//        return accessoryDTORepository.findAllProjectedBy();
    }





    private AccessoryDTO toDTO(Accessory acc) {
//        String imageUrl = acc.getImageData() != null
//                ? IMAGE_BASE_URL + acc.getAccessoryId()
//                : null;

        return AccessoryDTO.builder()
                .accessoryId(acc.getAccessoryId())
                .productName(acc.getProductName())
                .brandName(acc.getBrandName())
                .rating(acc.getRating())
                .description(acc.getDescription())
                .imageUrl(acc.getImageUrl())
                .accessoryCategory(acc.getAccessoryCategory())
                .build();
    }

//    @Override
//    @Transactional
//    public byte[] getImage(int id) {
////     Optional<Accessory> optionalAccessory = accessoryRepository.findById(id);
////        if (optionalAccessory.isEmpty() || optionalAccessory.get().getImageData() == null || optionalAccessory.get().getImageType() == null) {
////            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found for accessory ID: " + id);
////        }
////        Accessory accessory = optionalAccessory.get();
////        return "data:" + accessory.getImageType() + ";base64," + Base64.getEncoder().encodeToString(accessory.getImageData());
//
//        return accessoryRepository.findById(id).get().getImageData();
//
//    }
}

