package com.nazjara.service;

import com.nazjara.model.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions.AudioResponseFormat;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions.Voice;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAIServiceImpl implements OpenAIService {

  private final TextToSpeechModel speechModel;

  @Override
  public byte[] getAudio(Question question) {
    var options = OpenAiAudioSpeechOptions.builder()
        .voice(Voice.SAGE)
        .speed(1.0)
        .responseFormat(AudioResponseFormat.MP3)
        .model("gpt-4o-mini-tts")
        .instructions("Calm, friendly narrator")
        .build();

    var prompt = new TextToSpeechPrompt(question.question(), options);
    return speechModel.call(prompt).getResult().getOutput();
  }
}
