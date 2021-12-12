package com.crio.starter.exchange;

import com.crio.starter.dto.Meme;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMemesResponse {
  List<Meme> memes = new ArrayList<>();
}