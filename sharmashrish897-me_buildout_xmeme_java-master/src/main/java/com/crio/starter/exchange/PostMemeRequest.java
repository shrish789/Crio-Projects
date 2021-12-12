package com.crio.starter.exchange;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostMemeRequest {
    
  @NotNull(message = "Please provide name")
  String name;
  @NotNull(message = "Please provide url")
  String url;
  @NotNull(message = "Please provide caption")
  String caption;
}