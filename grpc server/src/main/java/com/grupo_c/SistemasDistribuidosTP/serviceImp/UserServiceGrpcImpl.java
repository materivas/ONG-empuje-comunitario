package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.configuration.security.JwtUtils;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.exception.user.*;
import com.grupo_c.SistemasDistribuidosTP.factory.ResponseFactory;
import com.grupo_c.SistemasDistribuidosTP.mapper.UserMapper;
import com.grupo_c.SistemasDistribuidosTP.service.*;
import com.grupo_c.SistemasDistribuidosTP.validator.UserValidator;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {
    private final JwtUtils jwtUtils;
    private final IRoleService roleService;
    private final IUserService userService;
    private final IEventService eventService;
    public UserServiceGrpcImpl(JwtUtils jwtUtils, IRoleService roleService, IUserService userService, IEventService eventService) {
        this.jwtUtils = jwtUtils;
        this.roleService = roleService;
        this.userService = userService;
        this.eventService = eventService;
    }
    @Override
    public void login(
            UserServiceClass.LoginRequest request,
            StreamObserver<UserServiceClass.UserWithTokenDTO> responseObserver
    ) {
        User userEntity;
        try {
            userEntity = userService.findByUsernameOrEmail(request.getUsername());
            userService.isUserValid(userEntity, request.getPassword());
        } catch (UsernameNotFoundException | UserNotFoundException usernameNotFoundException) {
            sendGrpcError(responseObserver, Status.NOT_FOUND, "ERROR: Inicio de sesión fallido. "+usernameNotFoundException.getMessage());
            return;
        } catch (InvalidPasswordException invalidPasswordException) {
            sendGrpcError(responseObserver, Status.INVALID_ARGUMENT, "ERROR: Inicio de sesión fallido. "+invalidPasswordException.getMessage());
            return;
        } catch (UserNotActiveException userNotActiveException) {
            sendGrpcError(responseObserver, Status.FAILED_PRECONDITION, "ERROR: Inicio de sesión fallido. "+userNotActiveException.getMessage());
            return;
        }

        //dado que manejamos autenticacion con jwt, creamos uno para cada usuario que se loguea en el sistema
        String accessToken = jwtUtils.createToken(userEntity, 1800000L); // token valido por media hora

        responseObserver.onNext(UserMapper.userToUserWithTokenDTO(userEntity, accessToken));
        responseObserver.onCompleted();
    }

    @Override
    public void createUser(
            UserServiceClass.UserWithRolesDTO request,
            StreamObserver<UtilsServiceClass.Response> responseObserver
    ) {
        try {
            UserValidator.isUserValid(request);
        } catch (UserNotValidException userNotValidException) {
            responseObserver.onNext(ResponseFactory.createResponse(
                    "ERROR: No se pudo registrar al usuario. "+userNotValidException.getMessage(),
                    false
            ));
            responseObserver.onCompleted();
            return;
        }

        try {
            userService.createUser(request, roleService.findAll());
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException |
                 PhoneNumberAlreadyExistsException alreadyExistsException) {
            responseObserver.onNext(ResponseFactory.createResponse(
                    "ERROR: No se pudo registrar al usuario. "+alreadyExistsException.getMessage(),
                    false
            ));
            responseObserver.onCompleted();
            return;
        }

        responseObserver.onNext(ResponseFactory.createResponse("Registro exitoso.", true));
        responseObserver.onCompleted();
    }

    @Override
    public void modifyUser(
            UserServiceClass.UserWithIdAndRolesDTO request,
            StreamObserver<UtilsServiceClass.Response> responseObserver
    ) {
        User userEntity;
        try {
            userEntity = userService.findById(request.getId());
        } catch(UserNotFoundException userNotFoundException) {
            responseObserver.onNext(ResponseFactory.createResponse(
                    "ERROR: Modificación fallida. "+userNotFoundException.getMessage(),
                    false
            ));
            responseObserver.onCompleted();
            return;
        }

        try {
            UserValidator.isUserValid(request.getUserWithRolesDTO());
        } catch (UserNotValidException userNotValidException) {
            responseObserver.onNext(ResponseFactory.createResponse(
                    "ERROR: Modificación fallida. "+userNotValidException.getMessage(),
                    false
            ));
            responseObserver.onCompleted();
            return;
        }

        try {
            userService.modifyUser(userEntity, request, roleService.findAll());
            if(!userEntity.getIsActive()) eventService.removeUserFromUpcomingEvents(userEntity);
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException | PhoneNumberAlreadyExistsException alreadyExistsException) {
            responseObserver.onNext(ResponseFactory.createResponse(
                    "ERROR: Modificación fallida. "+alreadyExistsException.getMessage(),
                    false
            ));
            responseObserver.onCompleted();
            return;
        }

        responseObserver.onNext(ResponseFactory.createResponse("Modificación exitosa.", true));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(
            UserServiceClass.UserIdDTO request,
            StreamObserver<UtilsServiceClass.Response> responseObserver
    ) {
        User userEntity;
        try {
            userEntity = userService.findById(request.getId());
        } catch(UserNotFoundException userNotFoundException) {
            responseObserver.onNext(ResponseFactory.createResponse(
                    "ERROR: Eliminación fallida. "+userNotFoundException.getMessage(),
                    false
            ));
            responseObserver.onCompleted();
            return;
        }

        userService.deleteUser(userEntity);
        eventService.removeUserFromUpcomingEvents(userEntity);

        responseObserver.onNext(ResponseFactory.createResponse("Eliminación exitosa.", true));
        responseObserver.onCompleted();
    }

    @Override
    public void getUserList(
            UtilsServiceClass.Empty request,
            StreamObserver<UserServiceClass.UserListResponse> responseObserver
    ) {
        responseObserver.onNext(UserMapper.usersToUserListResponse(userService.findAll()));
        responseObserver.onCompleted();
    }

    @Override
    public void getActiveUsersList(
            UtilsServiceClass.Empty request,
            StreamObserver<UserServiceClass.ActiveUsersListDTO> responseObserver
    ) {
        List<User> usersFromDB = userService.findByIsActiveTrue();
        List<UserServiceClass.UserSimpleDTO> usersSimpleDTO = UserMapper.usersEntityToUsersSimpleDTO(usersFromDB);
        UserServiceClass.ActiveUsersListDTO activeUsersListDTO = UserServiceClass.ActiveUsersListDTO
                .newBuilder()
                .addAllUsers(usersSimpleDTO)
                .build();
        responseObserver.onNext(activeUsersListDTO);
        responseObserver.onCompleted();
    }

    @Override
    public void getUser(
            UserServiceClass.UserUsernameDTO request,
            StreamObserver<UserServiceClass.UserWithIdAndRolesDTO> responseObserver
    ) {
        User userEntity;

        try {
            userEntity = userService.findByUsername(request.getUsername());
        } catch(UsernameNotFoundException usernameNotFoundException) {
            sendGrpcError(responseObserver, Status.NOT_FOUND, usernameNotFoundException.getMessage());
            return;
        }

        responseObserver.onNext(UserMapper.userEntityToUserWithIdAndRolesDTO(userEntity));
        responseObserver.onCompleted();
    }

    private void sendGrpcError(StreamObserver<?> responseObserver, Status status, String message) {
        responseObserver.onError(new StatusRuntimeException(status.withDescription(message)));
    }
}
