package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.entity.Role;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.exception.user.*;
import com.grupo_c.SistemasDistribuidosTP.mapper.UserMapper;
import com.grupo_c.SistemasDistribuidosTP.repository.IUserRepository;
import com.grupo_c.SistemasDistribuidosTP.service.IMailSenderService;
import com.grupo_c.SistemasDistribuidosTP.service.IUserService;
import com.grupo_c.SistemasDistribuidosTP.service.UserServiceClass;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements IUserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IMailSenderService mailSenderService;

    public UserServiceImpl(
            IUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            IMailSenderService mailSenderService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSenderService = mailSenderService;
    }

    @Override
    public User findById(long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("No existe ningún usuario con el ID específicado.")
        );
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("No existe ningún usuario registrado con ese nombre de usuario.")
        );
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findByUsernameOrEmail(String usernameOrEmail) throws UserNotFoundException {
        return userRepository.findByUsernameOrEmail(usernameOrEmail).orElseThrow(() ->
                new UserNotFoundException("No existe ningún usuario registrado con ese nombre de usuario o correo electrónico.")
        );
    }

    @Override
    public User findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email).orElse(null);
    }

    @Override
    public List<User> findByIsActiveTrue() {
        return userRepository.findByIsActiveTrue();
    }

    @Override
    public List<User> findByIsActiveFalse() {
        return userRepository.findByIsActiveFalse();
    }

    @Override
    public List<User> findByNameContainingIgnoreCase(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<User> findBySurnameContainingIgnoreCase(String surname) {
        return userRepository.findBySurnameContainingIgnoreCase(surname);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsernameAndIdNot(String username, long id) {
        return userRepository.existsByUsernameAndIdNot(username, id);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, long id) {
        return userRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public boolean existsByPhoneNumberAndIdNot(String phoneNumber, long id) {
        return userRepository.existsByPhoneNumberAndIdNot(phoneNumber, id);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsByEmailAndUsernameNot(String email, String username) {
        return userRepository.existsByEmailAndUsernameNot(email, username);
    }

    @Override
    public boolean existsByPhoneNumberAndUsernameNot(String phoneNumber, String username) {
        return userRepository.existsByPhoneNumberAndUsernameNot(phoneNumber, username);
    }

    @Override
    public List<User> findByRoleName(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    @Override
    public List<User> findByRoleNames(List<String> roleNames) {
        return userRepository.findByRoleNames(roleNames);
    }

    @Override
    public List<User> findAllActiveUsersOrdered() {
        return userRepository.findAllActiveUsersOrdered();
    }

    @Override
    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User userEntity) {
        return userRepository.save(userEntity);
    }

    @Override
    public void isUserValid(User userEntity, String password) throws UsernameNotFoundException, InvalidPasswordException, UserNotActiveException {
        if(userEntity == null) throw new UsernameNotFoundException("No existe ningún usuario registrado con ese nombre de usuario o correo electrónico.");
        if(!passwordEncoder.matches(password, userEntity.getPassword())) throw new InvalidPasswordException("Contraseña incorrecta.");
        if(!userEntity.getIsActive()) throw new UserNotActiveException("Este usuario fue eliminado.");
    }

    @Override
    public void createUser(UserServiceClass.UserWithRolesDTO userWithRolesDTO, List<Role> rolesFromDB) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, PhoneNumberAlreadyExistsException {
        if(existsByUsername(userWithRolesDTO.getUsername())) throw new UsernameAlreadyExistsException("El nombre de usuario indicado ya está en uso.");
        if(existsByEmail(userWithRolesDTO.getEmail())) throw new EmailAlreadyExistsException("El correo electrónico indicado ya está en uso.");
        if(existsByPhoneNumber(userWithRolesDTO.getPhoneNumber())) throw new PhoneNumberAlreadyExistsException("El número de telefono indicado ya está en uso.");

        User userEntity = new User();
        String generatedPassword = UUID.randomUUID().toString();
        UserMapper.userWithRolesDTOToUser(userWithRolesDTO, userEntity, rolesFromDB);
        userEntity.setPassword(passwordEncoder.encode(generatedPassword));
        User savedUser = save(userEntity);
        mailSenderService.send(savedUser, generatedPassword);
    }

    @Override
    public void modifyUser(User userEntity, UserServiceClass.UserWithIdAndRolesDTO userWithIdAndRolesDTO, List<Role> rolesFromDB) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, PhoneNumberAlreadyExistsException {
        UserServiceClass.UserWithRolesDTO userWithRolesDTO = userWithIdAndRolesDTO.getUserWithRolesDTO();
        if(existsByUsernameAndIdNot(userWithRolesDTO.getUsername(), userEntity.getId())) throw new UsernameAlreadyExistsException("El nombre de usuario indicado ya está en uso.");
        if(existsByEmailAndIdNot(userWithRolesDTO.getEmail(), userEntity.getId())) throw new EmailAlreadyExistsException("El correo electrónico indicado ya está en uso.");
        if(existsByPhoneNumberAndIdNot(userWithRolesDTO.getPhoneNumber(), userEntity.getId())) throw new PhoneNumberAlreadyExistsException("El número de telefono indicado ya está en uso.");

        UserMapper.userWithRolesDTOToUser(userWithRolesDTO, userEntity, rolesFromDB);
        userEntity.setModificationDate(LocalDateTime.now());
        save(userEntity);
    }

    @Override
    public void deleteUser(User userEntity) {
        userEntity.setIsActive(false);
        userEntity.setModificationDate(LocalDateTime.now());
        save(userEntity);
    }
}
