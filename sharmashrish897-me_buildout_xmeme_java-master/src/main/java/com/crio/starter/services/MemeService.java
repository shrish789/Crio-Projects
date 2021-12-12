package com.crio.starter.services;

import com.crio.starter.exchange.GetMemesResponse;
import com.crio.starter.exchange.GetMemesResponseById;
import com.crio.starter.exchange.PostMemeRequest;
import com.crio.starter.exchange.PostMemeResponse;

public interface MemeService {

  GetMemesResponse findMemes();

  GetMemesResponseById findMemesById(String id);

  PostMemeResponse postMeme(PostMemeRequest postMemeRequest);

}