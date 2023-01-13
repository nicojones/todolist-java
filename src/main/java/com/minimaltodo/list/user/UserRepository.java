package com.minimaltodo.list.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    // @Query("SELECT u FROM User WHERE u.email = ?1")
    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    List<User> findAllByEmail(String email);

    // @Query("SELECT u FROM User u JOIN FETCH u.projects p") 
    // List<User> findAlls();

}
