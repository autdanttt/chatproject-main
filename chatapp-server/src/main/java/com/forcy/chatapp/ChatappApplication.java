package com.forcy.chatapp;

import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

import static org.modelmapper.convention.MatchingStrategies.STRICT;

@SpringBootApplication
public class ChatappApplication {

	@Bean
	public Cloudinary cloudinaryConfig() {
		Cloudinary cloudinary = null;
		Map config = new HashMap();
		config.put("cloud_name", "dm8tfyppk");
		config.put("api_key", "923268916787181");
		config.put("api_secret", "2T3tP5KfBqZXB9mA4vvnKRNl6Zc");
		cloudinary = new Cloudinary(config);
		return cloudinary;
	}

	@Bean
	public ModelMapper getModelMapper(){
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(STRICT);
		return mapper;
	}
	@Bean
	public ObjectMapper objectMapper(){
		ObjectMapper objectMapper = new ObjectMapper();

		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

		return objectMapper;
	}
	public static void main(String[] args) {
		SpringApplication.run(ChatappApplication.class, args);
	}

}
