package com.backend.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@EnableOpenApi
@Configuration
public class SwaggerConfig {
    // Swagger 的配置信息 --> Docket
    @Bean
    public Docket docket(Environment environment) {
        // 设置要显示的环境
        Profiles profiles = Profiles.of("dev");
        // 判断是否为自己设定的环境中
//        boolean flag = environment.acceptsProfiles(profiles);
        boolean flag = true;


        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                // enable()，是否启动 Swagger，如果为 false，则不能通过浏览器访问 Swagger
                .enable(flag)
                // groupName()，设置分组，参数为组名
                // 如果要分多个组，就创建多个 Docket 对象即可
                .groupName("root")
                .select()
                // RequestHandlerSelectors，配置扫描接口的方式
                // basePackage()，指定要扫描的包
                // any()，全部扫描
                // none()，不进行扫描
                // withClassAnnotation()，扫描类上面的注解，需要传递一个注解类的反射对象 XXX.class
                // withMethodAnnotation()，扫描方法上面的注解
                .apis(RequestHandlerSelectors.any())
                // 路径过滤，也就是指定扫描的路径
                // ant()，指定路径，可以使用通配符
                // regex()，使用正则进行过滤
                .paths(PathSelectors.ant("/**"))
                .build();
    }

    // 配置 Swagger 信息 --> ApiInfo
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Swagger3 初步配置").version("1.0").build();
    }

}
