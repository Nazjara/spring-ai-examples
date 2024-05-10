package com.nazjara.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpenAIServiceImplTest {

    @Autowired
    OpenAIServiceImpl openAIService;

    @Test
    void testGetAnswer() {
        var answer = openAIService.getAnswer("Give me a dad joke");
        System.out.println(answer);
    }
}