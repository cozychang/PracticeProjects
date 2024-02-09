package com.reggie.config;

import com.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.cbor.MappingJackson2CborHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

//    设置静态资源映射
//    给resources下面的前端静态资源放行
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {


        registry.addResourceHandler("/backend/**")
                .addResourceLocations("classpath:/backend/");

        log.info("开始静态资源映射");
        registry.addResourceHandler("/front/**")
                .addResourceLocations("classpath:/front/");

    }

    //扩展mvc框架的消息转换器

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器JacksonObjectMapper
        mappingJackson2HttpMessageConverter.setObjectMapper(new JacksonObjectMapper());
        //将新建的消息转换器增加到mvc框架的转换器集合list中
        converters.add(0,mappingJackson2HttpMessageConverter);


    }
}
