package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.GetCapitalResponse;
import com.nazjara.model.Question;

public interface AiService {
	Answer getAnswer(Question question);
	GetCapitalResponse getCapital(String country, boolean extended);
}
