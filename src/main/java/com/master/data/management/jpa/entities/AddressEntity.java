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
@Entity(name = "address")
public class AddressEntity implements Serializable {

  @Id
  @GeneratedValue
  private Long id;

  @NotBlank
  private String postCode;

  @NotBlank
  private String streetAddressLine1;

  private String streetAddressLine2;

  @NotBlank
  private String city;

  @NotBlank
  private String country;

  @CreationTimestamp
  private LocalDateTime createdTimeStamp;

  @UpdateTimestamp
  private LocalDateTime updatedTimeStamp;
}
