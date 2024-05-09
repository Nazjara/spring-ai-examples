package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.Question;

public interface OpenAIService {
	Answer getAnswer(Question question);
	Answer getCapital(String country);
}
