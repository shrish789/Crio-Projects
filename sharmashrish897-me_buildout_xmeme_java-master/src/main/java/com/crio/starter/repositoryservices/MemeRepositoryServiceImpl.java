package com.crio.starter.repositoryservices;

import com.crio.starter.dto.Meme;
import com.crio.starter.models.MemeEntity;
import com.crio.starter.repository.MemeRepository;
import com.crio.starter.services.NextSequenceService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Provider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MemeRepositoryServiceImpl implements MemeRepositoryService {

  @Autowired
  private MemeRepository memeRepository;

  @Autowired
  private Provider<ModelMapper> modelMapperProvider;

  @Autowired
  private NextSequenceService nextSequenceService;

  @Override
  public List<Meme> findMemes() {

    List<Meme> memes = new ArrayList<>();
    List<MemeEntity> memeEntityList = memeRepository.findTop100ByOrderByIdDesc();
    ModelMapper modelMapper = modelMapperProvider.get();

    for (MemeEntity memeEntity : memeEntityList) {
      memes.add(modelMapper.map(memeEntity, Meme.class));
    }
    return memes;
  }

  @Override
  public Meme findMemesById(String id) {

    Optional<MemeEntity> memeEntityOptional = memeRepository.findById(Long.parseLong(id));
    
    if (!memeEntityOptional.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Meme with such Id");
    }

    MemeEntity memeEntity = memeEntityOptional.get();
    ModelMapper modelMapper = modelMapperProvider.get();
    Meme meme = modelMapper.map(memeEntity, Meme.class);

    return meme;
  }

  @Override
  public String postMeme(String name, String url, String caption) {

    Meme meme = new Meme();

    Optional<MemeEntity> memeEntityOptional = memeRepository
        .findByNameAndUrlAndCaption(name, url, caption);
    if (memeEntityOptional.isPresent()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate Entry");
    }

    meme.setId(Integer.toString(nextSequenceService.getNextSequence("customSequences")));
    meme.setName(name);
    meme.setUrl(url);
    meme.setCaption(caption);

    ModelMapper modelMapper = modelMapperProvider.get();
    MemeEntity memeEntity = modelMapper.map(meme, MemeEntity.class);
    memeRepository.save(memeEntity);
    return meme.getId();
  }

  public boolean checkIfDuplicate(String name, String url, String caption) {
    
    List<MemeEntity> memeEntityList = memeRepository.findAll();

    for (MemeEntity memeEntity : memeEntityList) {
      if (name.equals(memeEntity.getName()) && url.equals(memeEntity.getUrl())
          && caption.equals(memeEntity.getCaption())) {
        return true;
      }
    }
    
    return false;
  }

}