package dev.vkazulkin;

import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import dev.vkazulkin.controller.AuthorContentExtractorController;


@Import({AuthorContentExtractorController.class })
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
	@Bean ToolCallingManager toolCallingManager() {
		return ToolCallingManager.builder().build();
	}
}