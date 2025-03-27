package com.example.GridFSDemo.repository;

import com.example.GridFSDemo.model.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhotoRepository extends MongoRepository<Photo, String> {
}
