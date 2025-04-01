package com.nazjara.service;

import com.nazjara.model.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest.AudioResponseFormat;
import org.springframework.ai.openai.api.OpenAiAudioApi.SpeechRequest.Voice;
import org.springframework.ai.openai.api.OpenAiAudioApi.TtsModel;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAIServiceImpl implements OpenAIService {

  private final OpenAiAudioSpeechModel speechModel;

  @Override
  public byte[] getAudio(Question question) {
    var options = OpenAiAudioSpeechOptions.builder()
        .voice(Voice.SAGE)
        .speed(1.0f)
        .responseFormat(AudioResponseFormat.MP3)
        .model(TtsModel.TTS_1.getValue())
        .build();

    var prompt = new SpeechPrompt(question.question(), options);
    return speechModel.call(prompt).getResult().getOutput();
  }
}
