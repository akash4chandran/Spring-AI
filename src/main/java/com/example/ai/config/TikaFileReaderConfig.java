package com.example.ai.config;

import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

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
        TikaDocumentReader pdfDocumentReaderConfig = new TikaDocumentReader(resource);
        TokenTextSplitter textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(pdfDocumentReaderConfig.get()));
    }

    public void addResource(Resource resource) {
        TikaDocumentReader pdfDocumentReaderConfig = new TikaDocumentReader(resource);
        TokenTextSplitter textSplitter = new TokenTextSplitter();
        vectorStore.accept(textSplitter.apply(pdfDocumentReaderConfig.get()));
    }
}