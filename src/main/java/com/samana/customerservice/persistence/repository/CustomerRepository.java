package com.samana.customerservice.persistence.repository;

import com.samana.customerservice.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    List<CustomerEntity> findByNameContainingIgnoreCase(String name);
    List<CustomerEntity> findByEmailIgnoreCase (String email);
}
