package com.nazjara.service;

import com.nazjara.model.Question;

/**
 * Text-to-speech: turns text into spoken audio.
 */
public interface AiService {
  /**
   * Converts text to speech.
   *
   * @param question the text to speak
   * @return MP3 audio bytes
   */
  byte[] getAudio(Question question);
}
