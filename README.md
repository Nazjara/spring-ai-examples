# Spring AI Examples

This repository contains examples of using Spring AI with various AI models and capabilities.

## Prerequisites

- Java 25
- Maven (wrapper included)
- Anthropic API key (Claude) — all chat features
- OpenAI API key — only for image generation (`image`) and text-to-speech (`audio`)
- API Ninjas key — only for `functions`
- Docker — only for the `rag` `prod` profile (Milvus)

## Setup

1. Clone the repository
2. Set your API keys as environment variables:
   ```
   export ANTHROPIC_API_KEY=your_anthropic_key
   export OPENAI_API_KEY=your_openai_key
   export API_NINJAS_API_KEY=your_api_ninjas_key
   ```
3. Build the project:
   ```
   ./mvnw clean install
   ```

## Modules

### Basics

Demonstrates basic usage of Spring AI for text generation.

Features:
- Simple question answering
- Structured output generation

### Prompt Engineering

Shows advanced prompt engineering techniques with Spring AI.

Features:
- Chain of thought prompting
- Few-shot learning
- System message customization

This module has no endpoints; the examples are JUnit tests (`../mvnw test`).

### RAG (Retrieval Augmented Generation)

Implements Retrieval Augmented Generation for answering questions based on documents.

Features:
- Vector store integration (`SimpleVectorStore` by default, Milvus with the `prod` profile)
- Local ONNX embeddings (`all-MiniLM-L6-v2`) — no embedding API key needed
- Document similarity search
- Context-aware responses

Milvus: `cd rag && docker compose up -d`, then run with `-Dspring-boot.run.profiles=prod`.

### Functions

Demonstrates function calling capabilities with Spring AI.

Features:
- Weather information retrieval
- Function tool callbacks
- Structured data handling

### Image

Shows image generation and understanding capabilities.

Features:
- Image generation with DALL-E 3 (OpenAI)
- Image understanding with Claude

### Audio

Implements text-to-speech generation.

Features:
- Text-to-speech with OpenAI's `gpt-4o-mini-tts`
- Voice customization

## Usage

Each module can be run independently:

```
cd <module-name>
../mvnw spring-boot:run
```

Then you can interact with the APIs using tools like curl, Postman, or any HTTP client.

## API Examples

### Basics

```
POST /ask
Content-Type: application/json

{
  "question": "What is Spring AI?"
}
```

```
GET /capital?country=France&extended=true
```

### RAG

```
POST /ask
Content-Type: application/json

{
  "question": "What is the fuel consumption of the F150 outboard?"
}
```

### Functions

```
POST /weather
Content-Type: application/json

{
  "question": "What is the weather in Lviv, Ukraine?"
}
```

### Image Generation

```
POST /image
Content-Type: application/json

{
  "question": "A beautiful mountain landscape at sunset"
}
```

### Image Understanding

```
POST /vision
Content-Type: multipart/form-data

file=@picture.png
```

### Text-to-Speech

```
POST /audio
Content-Type: application/json

{
  "question": "Hello, this is a test of the text-to-speech functionality."
}
```