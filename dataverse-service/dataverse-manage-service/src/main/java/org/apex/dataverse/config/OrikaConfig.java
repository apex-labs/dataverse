package org.apex.dataverse.config;


import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.apex.dataverse.converter.LocalDateConvert;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: zoubin
 * @date: 11/23/18 15:25
 * @description: Orika 配置
 */
@Configuration
public class OrikaConfig {

    @Bean
    public MapperFactory mapperFactory() {
        return new DefaultMapperFactory.Builder().build();
    }

    @Bean
    public ConverterFactory converterFactory() {
        ConverterFactory converterFactory = mapperFactory().getConverterFactory();
        converterFactory.registerConverter(new LocalDateConvert());
        return converterFactory;
    }

}
