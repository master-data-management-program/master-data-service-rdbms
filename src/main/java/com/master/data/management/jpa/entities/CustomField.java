package com.master.data.management.jpa.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomField implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true)
  private String fieldName;

  private String fieldJson;

}
