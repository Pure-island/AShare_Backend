package com.backend.server.mapper;

import com.backend.server.entity.Appeal;
import com.backend.server.entity.Author;
import com.backend.server.entity.Coauthor;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CoauthorMapper extends MongoRepository<CoauthorMapper, Object> {
}
