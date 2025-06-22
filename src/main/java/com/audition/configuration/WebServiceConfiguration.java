package com.audition.configuration;

import com.audition.common.logging.AuditionLogger;
import com.audition.interceptor.ResponseHeaderInjector;
import com.audition.interceptor.RestClientLogInterceptor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@RequiredArgsConstructor
public class WebServiceConfiguration implements WebMvcConfigurer {

    private static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";

    final ResponseHeaderInjector responseHeaderInjector;
    final AuditionLogger auditionLogger;

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        //  1. allows for date format as yyyy-MM-dd
        // Configure date format with default Locale as it can be changed from sys args or properties
        objectMapper.setDateFormat(new SimpleDateFormat(YEAR_MONTH_DAY_PATTERN, Locale.getDefault()));
        //  5. does not write datas as timestamps.
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        //  2. Does not fail on unknown properties
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //  3. maps to camelCase
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        //  4. Does not include null values or empty values
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper());
        return converter;
    }

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate(
            new BufferingClientHttpRequestFactory(createClientFactory()));
        restTemplate.getMessageConverters().add(0, mappingJackson2HttpMessageConverter());
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        // Setting the auditionLogger along with Objectmapper
        interceptors.add(restClientLogInterceptor(auditionLogger, objectMapper()));

        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(responseHeaderInjector);
    }

    @Bean
    public ClientHttpRequestInterceptor restClientLogInterceptor(final AuditionLogger logger,
        final ObjectMapper objectMapper) {
        return new RestClientLogInterceptor(logger, objectMapper);
    }

    private SimpleClientHttpRequestFactory createClientFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        return requestFactory;
    }
}
