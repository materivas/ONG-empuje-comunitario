package com.grupo_c.SistemasDistribuidosTP.service;

import com.grupo_c.SistemasDistribuidosTP.entity.User;

public interface IMailSenderService {
    void send(User user, String password);
}
