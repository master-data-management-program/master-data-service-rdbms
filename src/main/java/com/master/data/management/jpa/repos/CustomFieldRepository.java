package com.master.data.management.jpa.repos;

import com.master.data.management.jpa.entities.CustomFieldEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFieldRepository extends JpaRepository<CustomFieldEntity, Long> {

  Optional<CustomFieldEntity> findByFieldName(Object fieldName);
}
