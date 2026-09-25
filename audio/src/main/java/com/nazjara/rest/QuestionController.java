package com.nazjara.rest;

import com.nazjara.model.Question;
import com.nazjara.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry point for text-to-speech.
 */
@RestController
@RequiredArgsConstructor
public class QuestionController {

	private final AiService aiService;

	/**
	 * Speaks the given text.
	 *
	 * @param question JSON body {@code {"question": "<text to speak>"}}
	 * @return MP3 audio
	 */
	@PostMapping(value = "/audio", produces = "audio/mpeg")
	public byte[] generateAudio(@RequestBody Question question) {
		return aiService.getAudio(question);
	}
}
