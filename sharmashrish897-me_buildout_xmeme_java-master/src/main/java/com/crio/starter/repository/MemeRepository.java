package com.crio.starter.repository;

import com.crio.starter.models.MemeEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemeRepository extends MongoRepository<MemeEntity, String> {

  Optional<MemeEntity> findById(long id);

  Optional<MemeEntity> findByNameAndUrlAndCaption(String name, String url, String caption);

  List<MemeEntity> findTop100ByOrderByIdDesc();
}