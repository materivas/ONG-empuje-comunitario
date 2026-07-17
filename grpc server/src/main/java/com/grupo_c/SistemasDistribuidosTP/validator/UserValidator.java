package com.grupo_c.SistemasDistribuidosTP.validator;

import com.grupo_c.SistemasDistribuidosTP.exception.user.UserNotValidException;
import com.grupo_c.SistemasDistribuidosTP.service.UserServiceClass;

import java.util.regex.Pattern;

public class UserValidator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_NUMBER_REGEX = "^\\+\\d{1,3}\\s\\d{1,4}\\s\\d{4}-\\d{4}$";
    public static void isUserValid(UserServiceClass.UserWithRolesDTO request) throws UserNotValidException {
        if(request.getUsername().isBlank()) throw new UserNotValidException("El nombre de usuario no puede estar vacío.");
        if(request.getName().isBlank()) throw new UserNotValidException("El nombre del usuario no puede estar vacío.");
        if(request.getSurname().isBlank()) throw new UserNotValidException("El apellido del usuario no puede estar vacío.");
        if(request.getEmail().isBlank()) throw new UserNotValidException("El email no puede estar vacío.");
        if(!request.getPhoneNumber().isBlank() && phoneNumberIsNotValid(request.getPhoneNumber())) throw new UserNotValidException("Formato de número de telefono incorrecto. Ejemplo con formato válido: +54 11 1234-5678");
        if(emailIsNotValid(request.getEmail())) throw new UserNotValidException("Formato de email incorrecto. Ejemplo con formato válido: email@dominio.com");
        if(request.getRolesList().isEmpty()) throw new UserNotValidException("El usuario debe tener asignado al menos un rol.");
    }
    public static boolean emailIsNotValid(String email) {
        return !Pattern.matches(EMAIL_REGEX, email);
    }
    public static boolean phoneNumberIsNotValid(String phoneNumber) {
        return !Pattern.matches(PHONE_NUMBER_REGEX, phoneNumber);
    }
}
