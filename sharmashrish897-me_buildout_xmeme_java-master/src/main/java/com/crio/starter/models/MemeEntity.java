package com.crio.starter.models;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "memes")
@NoArgsConstructor
public class MemeEntity {

  @Id
  private long id;
  @NotNull
  private String name;
  @NotNull
  private String url;
  @NotNull
  private String caption;
}