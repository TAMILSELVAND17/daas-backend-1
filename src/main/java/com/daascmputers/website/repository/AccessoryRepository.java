package com.daascmputers.website.repository;

import com.daascmputers.website.entities.Accessory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessoryRepository extends JpaRepository<Accessory, Integer> {

//    @Query("SELECT a.imageData FROM Accessory a WHERE a.accessoryId = :id")
//    Optional<byte[]> findImageDataById(@Param("id") int id);

//    @Query("SELECT a FROM Accessory a")
//    List<Accessory> fetchAll();
}
