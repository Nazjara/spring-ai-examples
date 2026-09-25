package com.nazjara.service;

import com.nazjara.model.Answer;
import com.nazjara.model.CapitalDetails;
import com.nazjara.model.GetCapitalResponse;
import com.nazjara.model.Question;
import java.util.List;

public interface AiService {
	Answer getAnswer(Question question);
	GetCapitalResponse getCapital(String country);
	CapitalDetails getCapitalDetails(String country);
	List<CapitalDetails> getCapitals(String region);
}
