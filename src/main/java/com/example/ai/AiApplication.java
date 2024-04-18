package com.example.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

import com.example.ai.config.PdfFileReaderConfig;
import com.example.ai.config.TikaFileReaderConfig;

@SpringBootApplication
public class AiApplication implements CommandLineRunner {

    private final TikaFileReaderConfig tikaFileReaderConfig;

    private final PdfFileReaderConfig pdfFileReaderConfig;
    @Value("classpath:Demo.pdf")
    private Resource pdfResource;

    @Value("classpath:Demo.csv")
    private Resource csvResource;

    public AiApplication(TikaFileReaderConfig tikaFileReaderConfig, PdfFileReaderConfig pdfFileReaderConfig) {
        this.tikaFileReaderConfig = tikaFileReaderConfig;
        this.pdfFileReaderConfig = pdfFileReaderConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(AiApplication.class, args);
    }

    @Override
    public void run(String... args) {
        pdfFileReaderConfig.addResource(pdfResource);
        tikaFileReaderConfig.addResource(csvResource);
    }
}