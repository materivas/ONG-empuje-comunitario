package com.grupo_c.SistemasDistribuidosTP.mapper;

import com.grupo_c.SistemasDistribuidosTP.entity.Role;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.service.UserServiceClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserMapper {
    public static Set<UserServiceClass.RoleDTO> roleEntityToRoleDTO(Set<String> roles) {
        Set<UserServiceClass.RoleDTO> rolesDTO = new HashSet<>();
        for(String role : roles)
            rolesDTO.add(UserServiceClass.RoleDTO.newBuilder().setName(role).build());
        return rolesDTO;
    }

    public static UserServiceClass.UserSimpleDTO userEntityToUserSimpleDTO(User userEntity) {
        return UserServiceClass.UserSimpleDTO
                .newBuilder()
                .setId(userEntity.getId())
                .setUsername(userEntity.getUsername())
                .build();
    }

    public static List<UserServiceClass.UserSimpleDTO> usersEntityToUsersSimpleDTO(List<User> usersEntity) {
        List<UserServiceClass.UserSimpleDTO> usersSimpleDTO = new ArrayList<>();
        for(User userEntity : usersEntity)
            usersSimpleDTO.add(userEntityToUserSimpleDTO(userEntity));
        return usersSimpleDTO;
    }

    public static UserServiceClass.UserWithRolesDTO userEntityToUserWithRolesDTO(User userEntity) {
        return UserServiceClass.UserWithRolesDTO
                .newBuilder()
                .setUsername(userEntity.getUsername())
                .setName(userEntity.getName())
                .setSurname(userEntity.getSurname())
                .setPhoneNumber(userEntity.getPhoneNumber())
                .setEmail(userEntity.getEmail())
                .setIsActive(userEntity.getIsActive())
                .addAllRoles(roleEntityToRoleDTO((userEntity.getRolesAsStrings())))
                .build();
    }

    public static UserServiceClass.UserWithIdAndRolesDTO userEntityToUserWithIdAndRolesDTO(User userEntity) {
        return UserServiceClass.UserWithIdAndRolesDTO
                .newBuilder()
                .setUserWithRolesDTO(userEntityToUserWithRolesDTO(userEntity))
                .setId(userEntity.getId())
                .build();
    }

    public static Set<Role> rolesDTOToRoles(List<UserServiceClass.RoleDTO> rolesDTO, List<Role> rolesFromDB) {
        Set<Role> roles = new HashSet<>();
        for(UserServiceClass.RoleDTO roleDTO : rolesDTO) {
            for(Role roleFromDB : rolesFromDB) {
                if(roleDTO.getName().equals(roleFromDB.getName())) {
                    roles.add(roleFromDB);
                    break;
                }
            }
        }
        return roles;
    }

    public static void userWithRolesDTOToUser(UserServiceClass.UserWithRolesDTO userWithRolesDTO, User userEntity, List<Role> rolesFromDB) {
        userEntity.setUsername(userWithRolesDTO.getUsername());
        userEntity.setName(userWithRolesDTO.getName());
        userEntity.setSurname(userWithRolesDTO.getSurname());
        userEntity.setPhoneNumber(userWithRolesDTO.getPhoneNumber());
        userEntity.setEmail(userWithRolesDTO.getEmail());
        userEntity.setIsActive(userWithRolesDTO.getIsActive());
        userEntity.setRoles(rolesDTOToRoles(userWithRolesDTO.getRolesList(), rolesFromDB));
    }

    public static UserServiceClass.UserListResponse usersToUserListResponse(List<User> users) {
        List<UserServiceClass.UserWithIdAndRolesDTO> usersWithIdAndRolesDTO = new ArrayList<>();
        for(User user : users) {
            usersWithIdAndRolesDTO.add(UserMapper.userEntityToUserWithIdAndRolesDTO(user));
        }

        return UserServiceClass.UserListResponse
                .newBuilder()
                .addAllUsers(usersWithIdAndRolesDTO)
                .build();
    }

    public static UserServiceClass.UserWithTokenDTO userToUserWithTokenDTO(User userEntity, String token) {
        return UserServiceClass.UserWithTokenDTO
                .newBuilder()
                .setUserWithRolesDTO(userEntityToUserWithRolesDTO(userEntity))
                .setToken(token)
                .build();
    }
}
