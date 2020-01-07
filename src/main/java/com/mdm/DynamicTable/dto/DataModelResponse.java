package com.mdm.DynamicTable.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataModelResponse {

  private String statusMessage;
  private HttpStatus httpStatus;

}
