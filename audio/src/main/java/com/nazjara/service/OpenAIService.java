package com.nazjara.service;

import com.nazjara.model.Question;

public interface OpenAIService {
  byte[] getAudio(Question question);
}
