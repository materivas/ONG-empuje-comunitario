package com.empujecomunitario.rest_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan(basePackages = "com.empujecomunitario.rest_server")
public class RestServerApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(RestServerApplication.class, args);

		System.out.println("DIAGNÓSTICO DE CONTROLLERS");

		String[] allBeans = ctx.getBeanDefinitionNames();
		for (String bean : allBeans) {
			if (bean.toLowerCase().contains("controller")) {
				System.out.println("CONTROLLER BEAN: " + bean);
			}
		}

		System.out.println("PAQUETE ACTUAL: " + RestServerApplication.class.getPackage().getName());

		try {
			Object controller = ctx.getBean("eventFilterController");
			System.out.println("EventFilterController ENCONTRADO: " + controller.getClass());
		} catch (Exception e) {
			System.out.println("EventFilterController NO ENCONTRADO: " + e.getMessage());
		}

		try {
			Object controller = ctx.getBean("eventReportController");
			System.out.println("EventReportController ENCONTRADO: " + controller.getClass());
		} catch (Exception e) {
			System.out.println("EventReportController NO ENCONTRADO: " + e.getMessage());
		}

		System.out.println("BUSCANDO MAPPINGS PARA /api/events/report");

	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins("http://localhost:8080")
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true);
			}
		};
	}
}