# CLAUDE.md

Pet project: multi-module Maven showcase of Spring AI features. Java 25, Spring Boot 4.1, Spring AI 2.0. Chat runs on Claude (Anthropic); OpenAI is used only where Anthropic has no equivalent (image generation, TTS). Each module is an independent Spring Boot app.

## Build & run

- Build all: `./mvnw clean install` (tests hit live model APIs — use `-DskipTests` when no key/credits). Needs JDK 25: the shell default may be 21, so prefix with `JAVA_HOME=/usr/lib/jvm/openjdk-25`.
- Run a module: `cd <module> && ../mvnw spring-boot:run`
- Required env: `ANTHROPIC_API_KEY` (all modules except `audio`); `OPENAI_API_KEY` for `image` and `audio`; `API_NINJAS_API_KEY` for `functions`
- `rag` prod profile needs Milvus: `cd rag && docker compose up -d`, then run with `-Dspring-boot.run.profiles=prod`

## Layout

Root `pom.xml` is the parent: Spring Boot parent, Spring AI BOM (`spring-ai.version`), and dependencies shared by all modules (webmvc, Anthropic starter, Lombok + its annotation processor path). Module poms only add module-specific deps (OpenAI starter in `image`/`audio`; Milvus, Tika, Transformers in `rag`). Never re-add `maven.compiler.*` properties to module poms — they override `java.version`.

| Module | Entry points | Spring AI surface |
|---|---|---|
| `basics` | `POST /ask`, `GET /capital?country=`, `GET /capital/details?country=`, `GET /capitals?region=` | `ChatClient`, `.st` templates via `.user(u -> u.text(resource).param(...))`, structured output via `.entity(...)` (record, `ParameterizedTypeReference<List<…>>`) |
| `prompt-engineering` | none — JUnit tests only | `ChatClient` via `BaseTestClass.chat(...)` |
| `rag` | `POST /ask` | `VectorStore` (`SimpleVectorStore` default, Milvus under `prod`), local ONNX `EmbeddingModel` (all-MiniLM-L6-v2, 384 dims), `TikaDocumentReader`, `TokenTextSplitter` |
| `functions` | `POST /weather` | `FunctionToolCallback` wrapping `WeatherServiceFunction` (api-ninjas) |
| `image` | `POST /image`, `POST /vision` (multipart) | `OpenAiImageModel` (generation); Claude via `ChatClient` + `Media` (vision) |
| `audio` | `POST /audio` | `TextToSpeechModel` (OpenAI `gpt-4o-mini-tts`) |
| `chat-memory` | `POST /chat/{window\|vector}/{id}` (+ `/stream` SSE), `GET /chat/window/{id}`, `GET /chat/vector/{id}?query=`, `DELETE /chat/{type}/{id}` | `MessageChatMemoryAdvisor` over `JdbcChatMemoryRepository` vs `VectorStoreChatMemoryAdvisor` over PgVector + local ONNX embeddings; `SimpleLoggerAdvisor` at DEBUG. Postgres via Boot Docker Compose — only with `spring-boot:run`; the pgvector image needs the `org.springframework.boot.service-connection=postgres` label |

Package convention per module (`com.nazjara`): `rest/QuestionController`, `service/AiService` + `AiServiceImpl`, `model/` records (`Question`, `Answer`), `configuration/`, `bootstrap/`.

## RAG specifics

- Documents listed in `ai.rag.documents-to-load[n]` (`VectorStoreProperties`), incl. remote URLs.
- Non-prod: `SimpleVectorStore` persisted to `ai.rag.vector-store-path` (`/tmp/vectorstore-minilm.json`); delete it to force re-ingestion. Changing the embedding model changes the vector dimension — use a new file/collection name rather than reusing old vectors.
- Prod: `MilvusVectorStoreLoader` ingests on startup if the store looks empty.
- `rag/volumes/` is Milvus docker data — never commit.

## Conventions

- Java records for DTOs; Lombok for `@Slf4j` / `@RequiredArgsConstructor`.
- Prompt templates live in `src/main/resources/templates` (or `template` in `rag`) as StringTemplate `.st` files.
- Model names are set in each module's `application.properties` (`spring.ai.anthropic.chat.model`, no `.options` segment in Spring AI 2.0).
- With both Anthropic and OpenAI starters on the classpath, select providers via `spring.ai.model.chat=anthropic` / `spring.ai.model.<type>=none` (see `image`, `audio`).
- New feature = new module registered in root `<modules>`, following the layout above.
