package com.minimaltodo.list.project;

import java.nio.file.AccessDeniedException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minimaltodo.list.user.User;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService service;

    @Autowired
    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, path = "")
    public ResponseEntity<List<Project>> getAllProjects(
        @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(service.getAllProjectsForUser(user));
    }

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<Project> addProject(
            @RequestBody Project project,
            @AuthenticationPrincipal User user) {

        return ResponseEntity.ok(service.saveProject(user, project));
    }
    
    @RequestMapping(method = RequestMethod.PUT, path = "")
    public ResponseEntity<Project> updateProject(
            @RequestBody Project project,
            @AuthenticationPrincipal User user) throws AccessDeniedException {

        return ResponseEntity.ok(service.updateProject(user, project));
    }
    
    @RequestMapping(method = RequestMethod.DELETE, path = "/{projectId}")
    public ResponseEntity<Long> deleteProject(
            @PathVariable("projectId") Long projectId,
            @AuthenticationPrincipal User user) throws AccessDeniedException {

        service.deleteProject(user, projectId);

        return ResponseEntity.ok(1L);
    }   

    @RequestMapping(method = RequestMethod.POST, path = "/{projectId}/join")
    public ResponseEntity<Long> addUserToProject(
            @PathVariable("projectId") Long projectId,
            @RequestParam("email") String newUserEmail,
            @AuthenticationPrincipal User user) throws AccessDeniedException {

        service.addUserToProject(user, projectId, newUserEmail);

        return ResponseEntity.ok(1L);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{projectId}/users")
    public ResponseEntity<List<String>> getUsersForProject(
            @PathVariable("projectId") Long projectId,
            @AuthenticationPrincipal User user
        ) throws AccessDeniedException {

        List<String> usersInProject = service.getUsersForProject(user, projectId)
            .stream()
            .map(u -> u.getEmail())
            .toList();

        return ResponseEntity.ok(usersInProject);
    }
    
    @RequestMapping(method = RequestMethod.DELETE, path = "/{projectId}/users")
    public ResponseEntity<Long> removeUserFromProject(
            @PathVariable("projectId") Long projectId,
            @RequestParam("email") String newUserEmail,
            @AuthenticationPrincipal User user) throws AccessDeniedException {

        service.removeUserFromProject(user, projectId, newUserEmail);

        return ResponseEntity.ok(1L);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "/{projectId}/all-tasks")
    public ResponseEntity<Long> deleteAllTasks(
            @PathVariable("projectId") Long projectId,
            @AuthenticationPrincipal User user) throws java.nio.file.AccessDeniedException {

        service.deleteAllTasks(user, projectId);

        return ResponseEntity.ok(1L);
    }
}
