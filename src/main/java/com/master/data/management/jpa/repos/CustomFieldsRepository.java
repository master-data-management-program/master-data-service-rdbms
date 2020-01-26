package com.master.data.management.jpa.repos;

import com.master.data.management.jpa.entities.CustomField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomFieldsRepository extends JpaRepository<CustomField, Long> {

}
