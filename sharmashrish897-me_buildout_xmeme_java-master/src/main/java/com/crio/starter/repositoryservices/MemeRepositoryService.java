package com.crio.starter.repositoryservices;

import com.crio.starter.dto.Meme;
import java.util.List;

public interface MemeRepositoryService {

  List<Meme> findMemes();

  Meme findMemesById(String id);

  String postMeme(String name, String url, String caption);
}