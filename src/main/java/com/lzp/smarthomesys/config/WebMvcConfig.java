package com.lzp.smarthomesys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${images.accessPath}")
    private String accessPathImage;

    @Value("${images.actualPath}")
    private String actualPathImage;

    @Value("${audio.accessPath}")
    private String accessPathAudio;

    @Value("${audio.actualPath}")
    private String actualPathAudio;

    /**
     * 配置静态资源路径
     * @param registry registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
            // addResourceHandler是指你想在url请求的路径
            // addResourceLocations是图片存放的真实路
            registry.addResourceHandler(accessPathImage + "**")
                    .addResourceLocations("file:" + actualPathImage);
            registry.addResourceHandler(accessPathAudio + "**")
                    .addResourceLocations("file:" + actualPathAudio);
            // 可以自定义资源处理类，对加载后的资源进行二次处理，比如图片统一打标识、解密之类的
            //      .resourceChain(true).addTransformer(new SecretImageResourceTransformerSupport();
    }

    /**
     * use: 跨域解决
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
