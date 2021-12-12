package com.crio.starter.services;

import com.crio.starter.dto.Meme;
import com.crio.starter.exchange.GetMemesResponse;
import com.crio.starter.exchange.GetMemesResponseById;
import com.crio.starter.exchange.PostMemeRequest;
import com.crio.starter.exchange.PostMemeResponse;
import com.crio.starter.repositoryservices.MemeRepositoryService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemeServiceImpl implements MemeService {

  @Autowired
  private MemeRepositoryService memeRepositoryService;

  @Override
  public GetMemesResponse findMemes() {
    // TODO Auto-generated method stub
    List<Meme> memes = memeRepositoryService.findMemes();
    return new GetMemesResponse(memes);
  }

  @Override
  public GetMemesResponseById findMemesById(String id) {
    // TODO Auto-generated method stub
    Meme meme = memeRepositoryService.findMemesById(id);
    return new GetMemesResponseById(meme.getId(), meme.getName(), meme.getUrl(), meme.getCaption());
  }

  @Override
  public PostMemeResponse postMeme(PostMemeRequest postMemeRequest) {
    // TODO Auto-generated method stub
    String postId = memeRepositoryService.postMeme(postMemeRequest.getName(), 
        postMemeRequest.getUrl(), postMemeRequest.getCaption());
    return new PostMemeResponse(postId);
  }

}