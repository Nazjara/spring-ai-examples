# Spring AI Examples

This repository contains examples of using Spring AI with various AI models and capabilities.

## Prerequisites

- Java 21
- Maven
- OpenAI API Key

## Setup

1. Clone the repository
2. Set your OpenAI API key as an environment variable:
   ```
   export OPENAI_API_KEY=your_api_key_here
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

### RAG (Retrieval Augmented Generation)

Implements Retrieval Augmented Generation for answering questions based on documents.

Features:
- Vector store integration
- Document similarity search
- Context-aware responses

### Functions

Demonstrates function calling capabilities with Spring AI.

Features:
- Weather information retrieval
- Function tool callbacks
- Structured data handling

### Image

Shows image generation and understanding capabilities.

Features:
- Image generation with DALL-E 3
- Image understanding with GPT-4o-mini

### Audio

Implements text-to-speech generation.

Features:
- Text-to-speech with OpenAI's TTS models
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

### Image Generation

```
POST /image
Content-Type: application/json

{
  "question": "A beautiful mountain landscape at sunset"
}
```

### Text-to-Speech

```
POST /audio
Content-Type: application/json

{
  "question": "Hello, this is a test of the text-to-speech functionality."
}
```