package com.grupo_c.SistemasDistribuidosTP.factory;

import com.grupo_c.SistemasDistribuidosTP.service.UtilsServiceClass;

public class ResponseFactory {
    public static UtilsServiceClass.Response createResponse(String message, boolean succeeded) {
        return UtilsServiceClass.Response
                .newBuilder()
                .setMessage(message)
                .setSucceeded(succeeded)
                .build();
    }
}
