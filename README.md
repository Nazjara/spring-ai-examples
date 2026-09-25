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
- Prompt templates (`.st` files with variables)
- Structured output with `ChatClient.entity(...)`: a single record, a richer typed record, and a `List` via `ParameterizedTypeReference`

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

### Chat Memory

Multi-turn chat that remembers the conversation, with two memory strategies side by side (`{memoryType}` = `window` or `vector`):

- **window** — `MessageChatMemoryAdvisor` + JDBC-backed `MessageWindowChatMemory`. Every request resends the last 20 messages as chat history; older ones are dropped.
- **vector** — `VectorStoreChatMemoryAdvisor` + PgVector with local ONNX embeddings. Every message is kept forever; each request retrieves only the 6 past messages most similar to the new question and adds them to the system prompt.

Features:
- Per-conversation IDs (required by Spring AI 2.0); `window` IDs are max 36 characters — UUIDs fit
- Streaming responses (Server-Sent Events)
- `SimpleLoggerAdvisor` logs the full request sent to the model — compare the two strategies in the console

PostgreSQL (with pgvector) is started automatically from `chat-memory/docker-compose.yml` by Spring Boot's Docker Compose support when run with `spring-boot:run` (Docker required).

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
GET /capital?country=France
GET /capital/details?country=France
GET /capitals?region=Scandinavia
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

### Chat Memory

```
POST /chat/{memoryType}/{conversationId}
Content-Type: application/json

{
  "question": "Hi, my name is Nazar"
}
```

```
POST /chat/{memoryType}/{conversationId}/stream   # same body, streamed as text/event-stream
GET /chat/window/{conversationId}                 # window: the history resent on each request
GET /chat/vector/{conversationId}?query=name      # vector: what would be recalled for this query
DELETE /chat/{memoryType}/{conversationId}        # forget the conversation
```
