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
				.description("만약 내가 핀테크페이를 만든다면? 이라는 생각으로 클론코딩을 해봤습니다.\n\n AWS EC2를 이용하여 호스팅 하였습니다.\n <개인 프로젝트>\n Github : https://github.com/oomm112/Fintechpay_"
						+ "\n-사용자인증 uri-\r\n"
						+ "https://twww.openbanking.or.kr/apt/mobileweb/authorizeNewGW?sessionID=a1618ba4-affa-416a-aa5d-7a59198bf612&action=Grant&api_tran_id=99c09294-788e-4ba2-bd15-9cce056de7f6&gw_svc_id=faf66bd6cafdf009a37caaac77ba5194&gw_app_key=6cc9ec2e-52e9-473a-a392-4a783fed7443&response_type=code&client_id=6cc9ec2e-52e9-473a-a392-4a783fed7443&client_info=test&redirect_uri=http://52.78.62.73:8080/openBanking/code&scope=login+inquiry+transfer&auth_type=0&lang=kor&state=b80BLsfigm9OokPTjy03elbJqRHOfGSY&account_hold_auth_yn=N&register_info=A&authorized_cert_yn=Y&cellphone_cert_yn=Y ")
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
