package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.service.IMailSenderService;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSenderServiceImpl implements IMailSenderService {
    private final JavaMailSender javaMailSender;
    private final SimpleMailMessage simpleMailMessage;
    public MailSenderServiceImpl(JavaMailSender javaMailSender, SimpleMailMessage simpleMailMessage) {
        this.javaMailSender = javaMailSender;
        this.simpleMailMessage = simpleMailMessage;
    }
    @Override
    public void send(User user, String password) {
        SimpleMailMessage message = new SimpleMailMessage(this.simpleMailMessage);
        message.setTo(user.getEmail());
        message.setText(
                "Querido "+user.getName()+" "+user.getSurname()+",\n\n"+
                "A la fecha de envíado este correo electrónico, fue dado de alta en el sistema ONG Empuje Comunitario con las siguientes credenciales:\n\n"+
                "Email: "+user.getEmail()+"\n"+
                "Nombre de usuario: "+user.getUsername()+"\n"+
                "Contraseña: "+password+"\n\n"+
                "Esperamos que tenga un excelente día.\nGestión de Usuarios - ONG Empuje Comunitario."
        );
        try {
            javaMailSender.send(message);
        } catch(MailException mailException) {
            mailException.printStackTrace(); //temporal. realmente deberiamos intentar reenviar el correo si por lo que sea falla
        }
    }
}
