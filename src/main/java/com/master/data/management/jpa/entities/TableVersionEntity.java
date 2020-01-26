package com.master.data.management.jpa.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Entity(name = "tableVersions")
public class TableVersionEntity implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true)
  private String tableName;

  @Column(columnDefinition = "text")
  private String effective;

  @Column(columnDefinition = "text")
  private String deltas;

  @CreationTimestamp
  private LocalDateTime createdTimeStamp;

  @UpdateTimestamp
  private LocalDateTime updatedTimeStamp;
}
