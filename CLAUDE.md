# CLAUDE.md

Pet project: multi-module Maven showcase of Spring AI features against OpenAI. Each module is an independent Spring Boot app.

## Build & run

- Build all: `./mvnw clean install` (tests hit the real OpenAI API — use `-DskipTests` when no key/credits)
- Run a module: `cd <module> && ../mvnw spring-boot:run`
- Required env: `OPENAI_API_KEY`; `functions` also needs `API_NINJAS_API_KEY`
- `rag` prod profile needs Milvus: `cd rag && docker compose up -d`, then run with `-Dspring-boot.run.profiles=prod`

## Layout

Root `pom.xml` is the parent: Spring Boot parent, Spring AI BOM (`spring-ai.version`), and dependencies shared by all modules (web, OpenAI starter, Tika reader, Lombok). Module poms only add module-specific deps.

| Module | Entry points | Spring AI surface |
|---|---|---|
| `basics` | `POST /ask`, `GET /capital?country=&extended=` | `ChatClient`, `PromptTemplate` (`templates/*.st`), `BeanOutputConverter` |
| `prompt-engineering` | none — JUnit tests only | `ChatClient` via `BaseTestClass.chat(...)` |
| `rag` | `POST /ask` | `VectorStore` (`SimpleVectorStore` default, Milvus under `prod`), `TikaDocumentReader`, `TokenTextSplitter` |
| `functions` | `POST /weather` | `FunctionToolCallback` wrapping `WeatherServiceFunction` (api-ninjas) |
| `image` | `POST /image`, `POST /vision` (multipart) | `OpenAiImageModel`, multimodal `UserMessage` + `Media` |
| `audio` | `POST /audio` | `OpenAiAudioSpeechModel` (TTS) |

Package convention per module (`com.nazjara`): `rest/QuestionController`, `service/OpenAIService` + `OpenAIServiceImpl`, `model/` records (`Question`, `Answer`), `configuration/`, `bootstrap/`.

## RAG specifics

- Documents listed in `ai.rag.documents-to-load[n]` (`VectorStoreProperties`), incl. remote URLs.
- Non-prod: `SimpleVectorStore` persisted to `ai.rag.vector-store-path` (`/tmp/vectorstore.json`); delete it to force re-ingestion.
- Prod: `MilvusVectorStoreLoader` ingests on startup if the store looks empty.
- `rag/volumes/` is Milvus docker data — never commit.

## Conventions

- Java records for DTOs; Lombok for `@Slf4j` / `@RequiredArgsConstructor`.
- Prompt templates live in `src/main/resources/templates` (or `template` in `rag`) as StringTemplate `.st` files.
- Model names are set in each module's `application.properties`.
- New feature = new module registered in root `<modules>`, following the layout above.
