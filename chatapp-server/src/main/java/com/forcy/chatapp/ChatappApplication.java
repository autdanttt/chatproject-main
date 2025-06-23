package com.forcy.chatapp;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.modelmapper.convention.MatchingStrategies.STRICT;

@SpringBootApplication
public class ChatappApplication {

	@Bean
	public ModelMapper getModelMapper(){
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(STRICT);
		return mapper;
	}

//	@Bean
//	public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
//		return builder
//				.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
//				.featuresToEnable(SerializationFeature.INDENT_OUTPUT)
//				.featuresToDisable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY) // 💥 TẮT sắp xếp alphabet
//				.build();
//	}
//	@Bean
//	public ObjectMapper objectMapper(){
//		ObjectMapper objectMapper = new ObjectMapper();
//
//		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
//		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
//
//		return objectMapper;
//	}
	public static void main(String[] args) {
		SpringApplication.run(ChatappApplication.class, args);
	}

}
