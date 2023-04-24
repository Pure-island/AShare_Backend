package com.backend.server.mapper;

import com.backend.server.entity.Aidpid;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AidpidMapper extends MongoRepository<Aidpid, Object> {
}
