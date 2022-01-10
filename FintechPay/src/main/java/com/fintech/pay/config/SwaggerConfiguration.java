package com.fintech.pay.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration implements WebMvcConfigurer{

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");

		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.securityContexts(Arrays.asList(securityContext()))
				.securitySchemes(Arrays.asList(apiKey()))
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.fintech.pay"))
				.paths(PathSelectors.any())
				.build();
	}


	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("핀테크페이 클론코딩")
				.description("만약 내가 핀테크페이를 만든다면? 이라는 생각으로 클론코딩을 해봤습니다.\n AWS EC2를 이용하여 호스팅 하였습니다.\n <개인 프로젝트>\n Github : https://github.com/oomm112/Fintechpay_"
						+ "\n-사용자인증 uri-\r\n"
						+ "https://testapi.openbanking.or.kr/oauth/2.0/authorize?response_type=code&client_id=6cc9ec2e-52e9-473a-a392-4a783fed7443&redirect_uri=http%3A%2F%2Flocalhost%3A9099%2FopenBanking%2Fcode&scope=login%20inquiry%20transfer&client_info=test&state=b80BLsfigm9OokPTjy03elbJqRHOfGSY&auth_type=0&cellphone_cert_yn=Y&authorized_cert_yn=Y&account_hold_auth_yn=N&register_info=A")
				.version("1.0.0")
				.build();
	}

	private SecurityContext securityContext() {
		return springfox
				.documentation
				.spi.service
				.contexts
				.SecurityContext
				.builder()
				.securityReferences(defaultAuth())
				.build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
	}

	private ApiKey apiKey() {
		return new ApiKey("JWT", "JWT", "header");
	}
}
