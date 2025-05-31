package com.springboot.resumehelper.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // 可以在这里进行自定义配置，例如:
        // modelMapper.getConfiguration().setFieldMatchingEnabled(true);
        return modelMapper;
    }
}