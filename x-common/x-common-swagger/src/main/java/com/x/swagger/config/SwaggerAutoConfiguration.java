package com.x.swagger.config;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
@EnableAutoConfiguration
@ConditionalOnProperty(name = "swagger.enabled", matchIfMissing = true)
public class SwaggerAutoConfiguration
{
    /**
     * 默认的排除路径，排除Spring Boot默认的错误处理路径和端点
     */
    private static final List<String> DEFAULT_EXCLUDE_PATH = Arrays.asList("/error", "/actuator/**");

    private static final String BASE_PATH = "/**";

    @Bean
    @ConditionalOnMissingBean
    public SwaggerProperties swaggerProperties()
    {
        return new SwaggerProperties();
    }

    @Bean
    public Docket api(SwaggerProperties swaggerProperties)
    {
        // base-path处理
        if (swaggerProperties.getBasePath().isEmpty())
        {
            swaggerProperties.getBasePath().add(BASE_PATH);
        }
        // noinspection unchecked
        List<Predicate<String>> basePath = new ArrayList<Predicate<String>>();
        swaggerProperties.getBasePath().stream().forEach(path -> basePath.add(PathSelectors.ant(path)));

        // exclude-path处理
        if (swaggerProperties.getExcludePath().isEmpty())
        {
            swaggerProperties.getExcludePath().addAll(DEFAULT_EXCLUDE_PATH);
        }
        List<Predicate<String>> excludePath = new ArrayList<>();
        swaggerProperties.getExcludePath().stream().forEach(path -> excludePath.add(PathSelectors.ant(path)));

         //noinspection Guava
        return new Docket(DocumentationType.SWAGGER_2)
                .host(swaggerProperties.getHost())
                .apiInfo(apiInfo(swaggerProperties)).select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .paths(Predicates.and(Predicates.not(Predicates.or(excludePath)), Predicates.or(basePath)))
                .build()
                .securitySchemes(securitySchemas())
                .securityContexts(Collections.singletonList(securityContext()))
                .pathMapping("/");
    }

     /**
      * 配置默认的全局鉴权策略的开关，通过正则表达式进行匹配；默认匹配所有URL
      *
      * @return
      */
    private SecurityContext securityContext()
    {
         return SecurityContext.builder()
             .securityReferences(defaultAuth())
             .forPaths(PathSelectors.regex(swaggerProperties().getAuthorization().getAuthRegex()))
             .build();
    }

     /**
      * 默认的全局鉴权策略
      *
      * @return
      */
    private List<SecurityReference> defaultAuth()
    {
        ArrayList<SecurityReference> securityReferences = new ArrayList<>();
        ArrayList<AuthorizationScope> authorizationScopeList = new ArrayList<>();
         swaggerProperties().getAuthorization().getAuthorizationScopeList().forEach(authorizationScope -> authorizationScopeList.add(new AuthorizationScope(authorizationScope.getScope(), authorizationScope.getDescription())));
         AuthorizationScope[] authorizationScopes = new AuthorizationScope[authorizationScopeList.size()];
         securityReferences.add(SecurityReference.builder()
             .reference(swaggerProperties().getAuthorization().getName())
             .scopes(authorizationScopeList.toArray(authorizationScopes))
             .build());
        securityReferences.add(SecurityReference.builder().reference("Authorization").scopes(new AuthorizationScope[]{new AuthorizationScope("global", "global des")}).build());
        securityReferences.add(SecurityReference.builder().reference("X-Customer-Id").scopes(new AuthorizationScope[]{new AuthorizationScope("global", "global des")}).build());
        securityReferences.add(SecurityReference.builder().reference("PAY_TOKEN").scopes(new AuthorizationScope[]{new AuthorizationScope("global", "global des")}).build());
         return securityReferences;

    }

    private List<SecurityScheme> securitySchemas()
    {
        ArrayList<SecurityScheme> securitySchemes = new ArrayList<>();
        securitySchemes.add(new ApiKey("Authorization", "Authorization", "header"));
        securitySchemes.add(new ApiKey("X-Customer-Id", "X-Customer-Id", "header"));
        securitySchemes.add(new ApiKey("PAY_TOKEN", "PAY_TOKEN", "header"));
        ArrayList<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        swaggerProperties().getAuthorization().getAuthorizationScopeList().forEach(authorizationScope -> authorizationScopeList.add(new AuthorizationScope(authorizationScope.getScope(), authorizationScope.getDescription())));
        ArrayList<GrantType> grantTypes = new ArrayList<>();
        swaggerProperties().getAuthorization().getTokenUrlList().forEach(tokenUrl -> grantTypes.add(new ResourceOwnerPasswordCredentialsGrant(tokenUrl)));
        securitySchemes.add(new OAuth(swaggerProperties().getAuthorization().getName(), authorizationScopeList, grantTypes));
        return securitySchemes ;
    }

    private ApiInfo apiInfo(SwaggerProperties swaggerProperties)
    {
         return new ApiInfoBuilder()
             .title(swaggerProperties.getTitle())
             .description(swaggerProperties.getDescription())
             .license(swaggerProperties.getLicense())
             .licenseUrl(swaggerProperties.getLicenseUrl())
             .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
             .contact(new Contact(swaggerProperties.getContact().getName(), swaggerProperties.getContact().getUrl(), swaggerProperties.getContact().getEmail()))
             .version(swaggerProperties.getVersion())
             .build();
    }
 }

