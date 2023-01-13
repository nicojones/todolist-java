package com.minimaltodo.list.task;

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
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    @Autowired
    public TaskController(TaskService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public ResponseEntity<List<Task>> getAllTasksForProject(
            @RequestParam("projectSecret") String projectSecret,
            @AuthenticationPrincipal User user) throws AccessDeniedException {

        switch (projectSecret) {
            case "inbox":
                return ResponseEntity.ok(service.getIncompleteTasks(user));
            case "priority":
                return ResponseEntity.ok(service.getPriorityTasks(user));
            default:
                return ResponseEntity.ok(service.getAllTasksForProject(user, projectSecret));
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/search/")
    public ResponseEntity<List<Task>> searchTasks(
            @RequestParam("q") String searchQuery,
            @AuthenticationPrincipal User user) throws AccessDeniedException {

        return ResponseEntity.ok(service.searchTasks(user, searchQuery));
    }

    @RequestMapping(method = RequestMethod.POST, path = "")
    public ResponseEntity<Task> addTask(
            @RequestBody Task task,
            @AuthenticationPrincipal User user)
            throws java.nio.file.AccessDeniedException {

        Task newTask = service.saveTask(task, user, task.getProjectId());
        return ResponseEntity.ok(newTask);
    }

    @RequestMapping(method = RequestMethod.PATCH, path = "/toggle/{taskId}")
    public ResponseEntity<Task> toggleTask(
            @PathVariable("taskId") Long taskId,
            @RequestParam("toggleSubtasks") boolean toggleSubtasks,
            @AuthenticationPrincipal User user) throws java.nio.file.AccessDeniedException {

        Task task = service.getTaskById(taskId, user);

        service.toggleTask(task, user);
        if (toggleSubtasks) {
            service.toggleSubtasks(task.getSubtasks(), task.isDone());
        }

        return ResponseEntity.ok(task);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "")
    public ResponseEntity<Task> updateTask(
            @RequestBody Task task,
            @AuthenticationPrincipal User user) throws java.nio.file.AccessDeniedException {

        service.updateTask(task, user, task.getProjectId());

        return ResponseEntity.ok(task);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/{taskId}")
    public ResponseEntity<Long> deleteTask(
            @PathVariable("taskId") Long taskId,
            @AuthenticationPrincipal User user) throws java.nio.file.AccessDeniedException {

        Task task = service.getTaskById(taskId, user);

        service.deleteTask(task);

        return ResponseEntity.ok(1L);
    }

}
