package com.nazjara.model;

public record AskResponse(String answer, boolean toolSearch, Integer promptTokens, Integer completionTokens, Integer totalTokens) {
}
