package com.crio.starter.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.crio.starter.dto.Meme;
import com.crio.starter.exchange.GetMemesResponse;
import com.crio.starter.services.MemeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

@AutoConfigureMockMvc
@SpringBootTest
public class MemeControllerTest {
    
  @Autowired
  private MockMvc mvc;

  @MockBean
  private MemeServiceImpl memeServiceMock;

  @InjectMocks
  private MemeController memeController;

  private ObjectMapper objectMapper;
  public static final String MEME_API_ENDPOINT = "/memes/";

  @BeforeEach
  public void setup() {
    objectMapper = new ObjectMapper();

    MockitoAnnotations.initMocks(this);

    mvc = MockMvcBuilders.standaloneSetup(memeController).build();
  }

  // @Test
  public void getMemesRespondsEmplyList() throws Exception {

    URI uri = UriComponentsBuilder.fromPath(MEME_API_ENDPOINT)
        .build().toUri();
    // List<Meme> memes = new ArrayList<>();
    // when(memeServiceMock.findMemes()).thenReturn(memes);
    MockHttpServletResponse response = null;
    try {
      response = mvc.perform(
          get(uri.toString())).andReturn().getResponse();
    } catch (NullPointerException e) {
      //TODO: handle exception
      System.out.println("Caught NullPointerException");
    }
    
    

    String responseStr = response.getContentAsString();
    // ObjectMapper mapper = new ObjectMapper();
    GetMemesResponse getMemesResponse = objectMapper.readValue(responseStr, GetMemesResponse.class);
    List<Meme> memes = new ArrayList<>();
    GetMemesResponse ref = new GetMemesResponse(memes);
    assertEquals(getMemesResponse, ref);
    
    // assertEquals(0, getMemesResponse.getMemes().size());

  }

}