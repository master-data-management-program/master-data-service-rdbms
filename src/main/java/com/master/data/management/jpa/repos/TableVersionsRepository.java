package com.master.data.management.jpa.repos;

import com.master.data.management.jpa.entities.TableVersionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableVersionsRepository extends JpaRepository<TableVersionEntity, Long> {

  Optional<TableVersionEntity> findByTableName(String tableName);
}
