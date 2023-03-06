package com.lzp.smarthomesys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${images.accessPath}")
    private String accessPath;

    @Value("${images.winPath}")
    private String winPath;

    @Value("${images.linuxPath}")
    private String linuxPath;
    /**
     * 配置静态资源路径
     * @param registry registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            // addResourceHandler是指你想在url请求的路径
            // addResourceLocations是图片存放的真实路
            registry.addResourceHandler(accessPath + "**")
                    .addResourceLocations("file:" + winPath);
            // 可以自定义资源处理类，对加载后的资源进行二次处理，比如图片统一打标识、解密之类的
            //      .resourceChain(true).addTransformer(new SecretImageResourceTransformerSupport();
        }else {
            registry.addResourceHandler(accessPath)
                    .addResourceLocations("file:" + linuxPath);
        }
    }

    /**
     * 跨域解决
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
