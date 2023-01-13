package com.minimaltodo.list.project;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.minimaltodo.list.user.User;


@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p.users FROM Project p WHERE p.id = ?1")
    List<User> getUsersForProject(Long projectId);
    
    @Query("SELECT u.projects FROM User u WHERE u.id = ?1") 
    List<Project> findAllForUser(Long userId);

    Project findProjectBySecret(String projectSecret);

    // @Query("SELECT p FROM Project p JOIN FETCH p.users u JOIN FETCH p.tasks t") 
    // List<Project> findAlls();
}
