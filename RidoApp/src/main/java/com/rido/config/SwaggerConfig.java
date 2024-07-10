//package com.rido.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//
//import io.swagger.v3.oas.annotations.OpenAPIDefinition;
//import io.swagger.v3.oas.annotations.info.Info;
//@OpenAPIDefinition(
//		info = @Info(  
//				title = "Rido_Project"
//				)
//		)
//@Configuration
////@EnableSwagger2
//public class SwaggerConfig {
//
////@Bean
////public Docket api(){
////  return new Docket(DocumentationType.SWAGGER_2)
////      .select()
////      .apis(RequestHandlerSelectors.any())
////      .paths(PathSelectors.regex("/user/.*"))
////      .build()
////      .apiInfo(apiInfo());
////}
////
////private ApiInfo apiInfo() {
////  return new ApiInfoBuilder()
////      .title("Rido App")
////      .description("This is a test of documenting EST API's")
////      .version("V1.2")
////      .termsOfServiceUrl("http://terms-of-services.url")
////      .license("LICENSE")
////      .licenseUrl("http://url-to-license.com")
////      .build();
////}
//
//}