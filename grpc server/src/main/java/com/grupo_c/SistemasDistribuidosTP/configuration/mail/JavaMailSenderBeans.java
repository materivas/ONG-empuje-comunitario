package com.grupo_c.SistemasDistribuidosTP.configuration.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class JavaMailSenderBeans {
    //SMTP_SERVER_HOST y SMTP_SERVER_PORT son valores temporales por ahora. hay que cambiarlos cuando dockerizemos el proyecto
    private final static String SMTP_SERVER_HOST = "mailhog";
    private final static int SMTP_SERVER_PORT = 1025;
    private final static String EMAIL = "gestion_usuarios@ongempujecomunitario.com";
    private final static String EMAIL_SUBJECT = "Alta de usuario";
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(SMTP_SERVER_HOST);
        javaMailSender.setPort(SMTP_SERVER_PORT);
        return javaMailSender;
    }
    @Bean
    public SimpleMailMessage simpleMailMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(EMAIL);
        simpleMailMessage.setSubject(EMAIL_SUBJECT);
        return simpleMailMessage;
    }
}
