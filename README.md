# Spring-AI
Spring AI is a project designed to simplify the development of AI-powered applications by providing a set of abstractions that work across different AI models and vector database providers. It offers support for major model providers like OpenAI, Microsoft, Amazon, Google, and Huggingface, covering model types such as chat and text-to-image, with more types planned for the future.

One of Spring AI's key features is its portable API, which allows developers to easily switch between different AI providers and vector database providers with minimal code changes. It also includes support for mapping AI model outputs to plain old Java objects (POJOs), making it easier to work with the results of AI computations.

Additionally, Spring AI offers support for various vector database providers, including Azure Vector Search, Chroma, Milvus, Neo4j, PostgreSQL/PGVector, PineCone, Qdrant, Redis, and Weaviate. Its ETL framework for data engineering and Spring Boot auto-configuration and starters for AI models and vector stores further simplify the development process.

# Vector Database
Vector databases are specialized databases crucial for AI applications, particularly for integrating data with AI models. Unlike traditional relational databases that focus on exact matches, vector databases perform similarity searches. When given a vector as a query, they return vectors that are "similar" to the query vector. This similarity calculation is key to their functionality.

In AI applications, vector databases are used to load data and retrieve similar documents for user queries. These documents serve as context for the AI model, enhancing its understanding of the user query. This approach, known as Retrieval Augmented Generation (RAG), improves the AI model's ability to provide relevant and accurate responses.

Spring AI provides an interface for using multiple vector database implementations, making it easier to integrate vector databases into AI applications. Understanding the concept of similarity searching in vector databases is crucial for effectively using these databases in AI applications.

# PGVector
PGVector is an open-source extension for PostgreSQL that facilitates storing and searching machine learning-generated embeddings. It enables users to identify exact and approximate nearest neighbors, enhancing the efficiency of similarity searches. PGVector integrates seamlessly with PostgreSQL's existing features, such as indexing and querying, making it a valuable tool for AI applications requiring vector storage and retrieval.

# Demo Project
This project aims to create an advanced conversational AI system by integrating OpenAI's GPT-4 model for chat functionality with Spring AI's abstractions for AI model integration. Additionally, the project includes support for reading PDF and other document formats using the Apache Tika library, with document content converted into embeddings using the PG Vector Store.

## Key Components:

- **OpenAI Integration** : Utilizing GPT-4 for generating human-like responses to user queries.
- **Spring AI Abstractions**: Simplifying AI model integration and supporting mapping of model outputs to Java objects.
- **Document Reader Support**: Reading and processing PDF and other document formats for content extraction.
- **PG Vector Store**: Storing document embeddings for efficient retrieval and similarity searches.
  
![Untitled Diagram1-Page-5 drawio](https://github.com/akash4chandran/Spring-AI/assets/35295545/e997bdff-ce56-4daa-a0ef-fdbf9eac7d3c)

# Run Postgres & PGVector DB locally
```Java
docker run -it --rm --name postgres -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres ankane/pgvector
```

# Configure the Maven project
```Java
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-pgvector-store</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-openai</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-pdf-document-reader</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-tika-document-reader</artifactId>
        </dependency>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.ai</groupId>
                <artifactId>spring-ai-bom</artifactId>
                <version>0.8.1</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    <repositories>
        <repository>
            <id>spring-milestones</id>
            <name>Spring Milestones</name>
            <url>https://repo.spring.io/milestone</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>
```
- spring-ai-pgvector-store: This dependency provides the Spring AI integration for the PG Vector Store, enabling you to store and retrieve document embeddings in PostgreSQL.
- spring-ai-openai: This dependency provides the Spring AI integration for OpenAI, allowing you to use OpenAI's GPT-4 model for chat functionality.
- spring-ai-pdf-document-reader: This dependency provides the Spring AI integration for reading PDF documents, enabling your application to extract text content from PDF files.
- spring-ai-tika-document-reader: This dependency provides the Spring AI integration for the Apache Tika document reader, which allows your application to extract text content from various document formats supported by Tika.

# Setup the application.properties
- Sign Up for OpenAI: If you haven't already, sign up for an account on the OpenAI website.
- Generate API Key: Once you're logged in to your OpenAI account, navigate to the API section and generate an API key. This key will be used to authenticate your requests to the OpenAI API. Update the property "SPRING_AI_OPENAI_API_KEY" with your actual API key.
  ```Java
  spring.ai.openai.api-key=${SPRING_AI_OPENAI_API_KEY}
  spring.ai.openai.embedding.enabled=true
  spring.ai.openai.chat.model=gpt-4
  spring.ai.openai.embedding.model=text-embedding-ada-002
  spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
  spring.datasource.username=postgres
  spring.datasource.password=postgres
  spring.ai.vectorstore.pgvector.index-type=HNSW
  spring.ai.vectorstore.pgvector.distance-type=COSINE_DISTANCE
  spring.ai.vectorstore.pgvector.dimension=1536
  ```
  Enable the embedding model of OpenAI by setting the "spring.ai.openai.embedding.enabled" to true and  add a desired model of your choice to the chat and     embedding models.

# Setup Configuration files
  ## OpenAI Configuration Class
  The OpenAIConfig class defines a configuration for integrating OpenAI into your Spring application
  ```Java
  @Configuration
  public class OpenAiConfig {

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Bean
    public OpenAiApi openAiApi() {
        return new OpenAiApi(openAiApiKey);
    }

    @Bean
    public EmbeddingClient openAiEmbeddingClient(OpenAiApi openAiApi) {
        return new OpenAiEmbeddingClient(openAiApi);
    }

    @Bean
    public ChatClient openAiChatClient(OpenAiApi openAiApi) {
        return new OpenAiChatClient(openAiApi);
    }

    @Bean
    public PgVectorStore pgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingClient openAiEmbeddingClient) {
        return new PgVectorStore(jdbcTemplate, openAiEmbeddingClient);
    }
    }
  ```
- The ChatClient class is responsible for interacting with OpenAI's chat functionality.
- The EmbeddingClient class is responsible for interacting with OpenAI's embedding functionality.
  
## PdfFileReader Configuration Class
  The PdfFileReaderConfig class is a Spring component responsible for configuring and processing PDF files for integration with the PgVectorStore for storing   document embeddings.
 ```Java
  @Component
  public class PdfFileReaderConfig {
    private final PgVectorStore vectorStore;

    public PdfFileReaderConfig(PgVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void addResource(Resource pdfResource) {
        PdfDocumentReaderConfig pdfDocumentReaderConfig = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().build()).build();
        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(pdfResource, pdfDocumentReaderConfig);
        TokenTextSplitter textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(pagePdfDocumentReader.get()));

    }
 ```
## TikaFileReader Configuration Class
The TikaFileReaderConfig class uses Apache Tika to process files in a directory, extracting text content from various file formats. It then converts the text content into embeddings and stores them in the PgVectorStore. By leveraging Apache Tika's capabilities, the class enables your application to handle a wide range of file formats and enhance its document processing functionality. Supported file formats can be verified at Apache Tika Supported Formats.
```Java
@Component
public class TikaFileReaderConfig {
    private final PgVectorStore vectorStore;

    public TikaFileReaderConfig(PgVectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void processFilesInDirectory(String directoryPath) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            paths.filter(Files::isRegularFile).forEach(this::addResource);
        }

    }

    public void addResource(Path path) {
        Resource resource = new FileSystemResource(path.toFile());
        TikaDocumentReader tikaDocumentReaderConfig = new TikaDocumentReader(resource);
        TokenTextSplitter textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(tikaDocumentReaderConfig.get()));
    }

    public void addResource(Resource resource) {
        TikaDocumentReader tikaDocumentReaderConfig = new TikaDocumentReader(resource);
        TokenTextSplitter textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(tikaDocumentReaderConfig.get()));
    }
}
```
- processFilesInDirectory Method: This method takes  the directory path as input and processes all files in the specified directory. It uses Files.walk to traverse the directory and forEach to process each file by calling the addResource method.
- addResource(Path path) Method: This method processes a single file specified by the Path parameter. It creates a Resource object from the file, initializes a TikaDocumentReader with the resource, and then uses a TokenTextSplitter to split the text content into tokens. Finally, it stores the tokens as embeddings in the PgVectorStore using vectorStore.accept.
- addResource(Resource resource) Method: This overloaded method is similar to the previous one but takes a Resource object directly instead of a Path. It initializes a TikaDocumentReader with the resource and processes it in the same way as the other addResource method.

## OpenAiChatClient Configuration Class
The AiChatClientConfig class configures and uses OpenAI's chat functionality to interact with users. It utilizes  PgVectorStore context-aware responses  ChatClient to communicate with the OpenAI chat API. This class enhances the chat capabilities of your application, providing a more engaging user experience.
```Java
@Component
public class OpenAiChatClientConfig {
   private String template = """
            You're assisting with questions about the employees working in the company.
            Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
            If unsure, simply state that you don't know.
            DOCUMENTS:
            {documents}
            """;
    private final PgVectorStore pgVectorStore;
    private final ChatClient openAiChatClient;

    public OpenAiChatClientConfig(PgVectorStore pgVectorStore, ChatClient openAiChatClient) {
        this.pgVectorStore = pgVectorStore;
        this.openAiChatClient = openAiChatClient;
    }

    public String chat(String message) {
        List<Document> documents = this.pgVectorStore.similaritySearch(message);
        String collect =
                documents.stream().map(Document::getContent).collect(Collectors.joining(System.lineSeparator()));
        Message createdMessage = new SystemPromptTemplate(template).createMessage(Map.of("documents", collect));
        UserMessage userMessage = new UserMessage(message);
        Prompt prompt = new Prompt(List.of(createdMessage, userMessage));
        ChatResponse chatResponse = openAiChatClient.call(prompt);
        return chatResponse.getResults().stream().map(generation -> {
            return generation.getOutput().getContent();
        }).collect(Collectors.joining("/n"));
    }

}
```
- Template String: The class includes a template string that defines the structure of the chat message. This template guides the AI to assist with questions related to a specific topic and references relevant information for context.
- This method takes  message as input parameters. It first performs a similarity search  PgVectorStore to find documents similar to the input message. It then creates a message using a SystemPromptTemplate and the documents found in the similarity search. Next, it creates a Prompt containing the user's message and the system's message. Finally, it calls  openAiChatClient to generate a response based on the prompt and returns the response content.

# Setup Controller class
```Java
@RestController
@RequestMapping("/ai")
public class AiController {
    private final AiChatClientConfig chatClient;

    @Value("classpath:/prompts/assist.st")
    private Resource assist;

    public AiController(AiChatClientConfig chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping
    public String retrieveData(@RequestParam(name = "message") String message) {
        return chatClient.chat(message, assist);
    }
}
```
# Conclusion
Overall, this article serves as a guide for developers looking to enhance their conversational AI capabilities using OpenAI and Spring AI, leveraging advanced embedding and chat models for more intelligent and context-aware responses.

# Reference
[Spring AI](https://docs.spring.io/spring-ai/reference/index.html)

