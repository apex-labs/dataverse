package org.apex.dataverse.config;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;


/**
 * swagger2配置
 *
 * @author danny
 *
 * @date 2019-04-03
 */
@Configuration
@EnableSwagger2
public class Swagger2Config  {


    private static final Collection<String> BASE_PACKAGE_NAMES = Collections
            .unmodifiableCollection(Arrays.asList("org.apex.dataverse.controller"));

    @Bean
    public Docket api() {
        List<Predicate<RequestHandler>> predicates = new ArrayList<Predicate<RequestHandler>>();
        for (String packageName : BASE_PACKAGE_NAMES) {
            predicates.add(RequestHandlerSelectors.basePackage(packageName));
        }
        List<Predicate<String>> basePath = new ArrayList();
        basePath.add(PathSelectors.ant("/**"));

        List<Predicate<String>> excludePath = new ArrayList<>();
        excludePath.add(PathSelectors.ant("/error"));
        excludePath.add(PathSelectors.ant("/actuator/**\""));
        Predicate<RequestHandler> predicate = Predicates.or(predicates);
//        return new Docket(DocumentationType.SWAGGER_2).
//                apiInfo(apiInfo()).
//                select().
//                apis(predicate).
//                paths(PathSelectors.any()).build();
        return new Docket(DocumentationType.SWAGGER_2).
                apiInfo(apiInfo()).enable(true).
                select().
                apis(RequestHandlerSelectors.basePackage("org.apex.dataverse.controller")).
                paths(PathSelectors.any()).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("rest接口在线api文档")
                .contact(new Contact("wwd","","wending.wang@chinapex.com"))
                .description("来自dataverse")
                .termsOfServiceUrl("https://swagger.io/")
                .version("1.0")
                .build();
    }



}
