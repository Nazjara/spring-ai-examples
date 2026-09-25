package com.nazjara.service;

import com.nazjara.model.Question;

public interface AiService {
  byte[] getAudio(Question question);
}
