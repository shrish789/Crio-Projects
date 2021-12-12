package com.crio.starter.controller;

import com.crio.starter.dto.Meme;
import com.crio.starter.exchange.GetMemesResponse;
import com.crio.starter.exchange.GetMemesResponseById;
import com.crio.starter.exchange.PostMemeRequest;
import com.crio.starter.exchange.PostMemeResponse;
import com.crio.starter.services.MemeService;

import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MemeController {

  public static final String MEME_API_ENDPOINT = "/memes/";

  @Autowired
  private MemeService memeService;

  @GetMapping(MEME_API_ENDPOINT)
  public ResponseEntity<List<Meme>> getMemes() {

    GetMemesResponse getMemesResponse;

    getMemesResponse = memeService.findMemes();
    return new ResponseEntity<List<Meme>>(getMemesResponse.getMemes(), HttpStatus.OK);
  }

  @GetMapping(MEME_API_ENDPOINT + "{id}")
  public ResponseEntity<GetMemesResponseById> getMemesById(
      @PathVariable String id) {

    GetMemesResponseById getMemesResponseById;
    getMemesResponseById = memeService.findMemesById(id);

    return ResponseEntity.ok().body(getMemesResponseById);
  }

  @PostMapping(MEME_API_ENDPOINT)
  public ResponseEntity<PostMemeResponse> postMeme(
      @Valid @RequestBody PostMemeRequest postMemeRequest) {

    PostMemeResponse postMemeResponse;
    postMemeResponse = memeService.postMeme(postMemeRequest);
    return ResponseEntity.ok().body(postMemeResponse);
  }

}