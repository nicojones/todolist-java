package com.minimaltodo.list.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Sort;

import com.minimaltodo.list.project.Project;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // @Query(value = "SELECT t.* FROM task t WHERE t.project_id = ?1 AND t.level = 1", nativeQuery = true)
    @Query(value = "SELECT t FROM Task t WHERE project = ?1 AND t.level = 1")
    public List<Task> findTaskByProject(Project project, Sort sort);
    
    
    @Query(value = "SELECT t FROM Task t WHERE project.id in ?1 AND t.level = 1 AND t.done = 0 ORDER BY t.priority DESC")
    public List<Task> findIncompleteTasks(List<Long> projectIds);

    @Query(value = "SELECT t FROM Task t WHERE project.id in ?1 AND t.level = 1 AND t.done = 0 AND t.priority != 0 ORDER BY t.priority DESC")
    public List<Task> findPriorityTasks(List<Long> projectIds);

    public Task findTaskBySecret(String taskSecret);

    // @Query("SELECT t FROM Task t JOIN FETCH t.project p JOIN FETCH t.subtasks s") 
    // List<Task> findAlls();
    
    // @Query("SELECT t FROM Task t JOIN t.project p WHERE t.project IN (SELECT u.projects FROM User u WHERE u.id = ?1)") 
    @Query("SELECT t, 1 as level FROM Task t JOIN t.project p WHERE t.project IN (SELECT u.projects FROM User u WHERE u.id = ?1) AND (t.name LIKE %?2% OR t.description LIKE %?2%)") 
    List<Task> searchAllTasks(Long userId, String query);
}
