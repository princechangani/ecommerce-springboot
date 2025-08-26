package com.project.repository;

import com.project.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByUserId(Long userId);
    
    List<Address> findByUserIdAndIsDefaultTrue(Long userId);
    
    List<Address> findByUserIdAndType(Long userId, String type);
    
    List<Address> findByUserIdAndTypeAndIsDefaultTrue(Long userId, String type);
}
