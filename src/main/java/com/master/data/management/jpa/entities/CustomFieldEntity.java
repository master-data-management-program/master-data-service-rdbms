package com.master.data.management.jpa.entities;

import static java.util.Objects.nonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity(name = "customFields")
public class CustomFieldEntity implements Serializable {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", updatable = false, nullable = false)
  @Setter(AccessLevel.NONE)
  private Long id;

  @Id
  private String fieldName;

  @NotBlank
  private String customFieldJson;

  @CreationTimestamp
  private LocalDateTime createdTimeStamp;

  @UpdateTimestamp
  private LocalDateTime updatedTimeStamp;

  public void setId(Long id) {
    if (nonNull(id)) {
      this.id = id;
    } else {
      this.id = ZonedDateTime.now().toEpochSecond();
    }
  }
}
