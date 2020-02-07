package com.master.data.management.jpa.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class CustomFieldEntity implements Serializable {

  @GeneratedValue
  private Long id;

  @Id
  private String fieldName;

  @NotBlank
  private String customFieldJson;

  @CreationTimestamp
  private LocalDateTime createdTimeStamp;

  @UpdateTimestamp
  private LocalDateTime updatedTimeStamp;
}
