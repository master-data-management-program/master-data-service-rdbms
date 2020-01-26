package com.master.data.management.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomFieldDTO{

  @Column(unique = true)
  private String fieldName;

  private JSONObject fieldJson;

}
