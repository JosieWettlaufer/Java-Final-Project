package com.example.GridFSDemo.repository;


import com.example.GridFSDemo.model.TimeCapsule;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;

public interface TimeCapsuleRepository extends MongoRepository<TimeCapsule, String> {
    List<TimeCapsule> findByUserId(String userId);
    List<TimeCapsule> findByLockDate(LocalDate lockDate);
}


