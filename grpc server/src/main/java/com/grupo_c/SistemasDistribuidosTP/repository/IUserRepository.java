package com.grupo_c.SistemasDistribuidosTP.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.grupo_c.SistemasDistribuidosTP.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);

    @Query(value = "SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    Optional<User> findByUsernameOrEmail(String username, String email);
    
    List<User> findByIsActiveTrue();
    
    List<User> findByIsActiveFalse();
    
    List<User> findByNameContainingIgnoreCase(String name);
    
    List<User> findBySurnameContainingIgnoreCase(String surname);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUsernameAndIdNot(String username, long id);

    boolean existsByEmailAndIdNot(String email, long id);

    boolean existsByPhoneNumberAndIdNot(String phoneNumber, long id);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.username <> :username")
    boolean existsByEmailAndUsernameNot(@Param("email") String email, 
                                      @Param("username") String username);
    
    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber AND u.username <> :username")
    boolean existsByPhoneNumberAndUsernameNot(@Param("phoneNumber") String phoneNumber, 
                                            @Param("username") String username);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name IN :roleNames")
    List<User> findByRoleNames(@Param("roleNames") List<String> roleNames);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true ORDER BY u.surname, u.name")
    List<User> findAllActiveUsersOrdered();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.events WHERE u.id = :userId")
    Optional<User> findByIdJoinEvents(@Param("userId") Long userId);
}