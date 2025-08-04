package com.daascmputers.website.repository;

import com.daascmputers.website.dto.AccessoryDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessoryDTORepository extends JpaRepository<AccessoryDTO,Integer> {
List<AccessoryDTO> findAllProjectedBy();
}
