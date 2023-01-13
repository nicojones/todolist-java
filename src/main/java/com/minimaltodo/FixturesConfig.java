package com.minimaltodo;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.minimaltodo.config.SecretGenerator;
import com.minimaltodo.list.project.Project;
import com.minimaltodo.list.project.ProjectIcon;
import com.minimaltodo.list.project.ProjectRepository;
import com.minimaltodo.list.project.ProjectSort;
import com.minimaltodo.list.task.Task;
import com.minimaltodo.list.task.TaskRepository;
import com.minimaltodo.list.user.Role;
import com.minimaltodo.list.user.User;
import com.minimaltodo.list.user.UserRepository;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class FixturesConfig {

    @Bean
    CommandLineRunner commandLineRunner(
            UserRepository ur,
            ProjectRepository pr,
            TaskRepository tr) {
        return args -> {
                SecretGenerator secret = new SecretGenerator();
            User user1 = new User(
                    "Nico Kupfer",
                    "nico@kupfer.es",
                    "$2a$10$XHPH5eONT0XdrDTdYqlt0uTtcp68MA.TTd.4f4Fd7oKBk7XUzcLLy",
                    true,
                    Role.USER);
            User user2 = new User(
                    "Petra Enbuske",
                    "petra@example.com",
                    "$2a$10$XHPH5eONT0XdrDTdYqlt0uTtcp68MA.TTd.4f4Fd7oKBk7XUzcLLy",
                    true,
                    Role.USER);

            ur.saveAll(List.of(user1, user2));
            List<User> allUsers = ur.findAll();

            Project project1 = new Project(
                    secret.generate(),
                    ProjectSort.A_TO_Z,
                    false,
                    "My List",
                    "#EB144C",
                    false,
                    ProjectIcon.FAVORITE
                    );
                    Project project2 = new Project(
                    secret.generate(),
                    ProjectSort.PRIORITY,
                    false,
                    "Must Do Soon",
                    "#0693E3",
                    false,
                    ProjectIcon.EXTENSION
                    );
                    Project project3 = new Project(
                    secret.generate(),
                    ProjectSort.Z_TO_A,
                    false,
                    "Movies",
                    "#FCB900",
                    false,
                    ProjectIcon.PHOTO
                    );


            project1.setUsers(List.of(allUsers.get(0), allUsers.get(1)));
            project2.setUsers(List.of(allUsers.get(0)));
            project3.setUsers(List.of(allUsers.get(0))); 

            pr.saveAll(List.of(project1, project2, project3));
            List<Project> allProjects = pr.findAll();

            Task task2 = new Task(secret.generate(), "Subtask","Some description",false,false,allProjects.get(0),2,1);
            Task task1 = new Task(secret.generate(), "Create list","Subtask description",false,true,allProjects.get(0),1,1);
            Task task3 = new Task(secret.generate(), "Second Task","Some other description",false,false,allProjects.get(0),1,1);
            Task task4 = new Task(secret.generate(), "Important","Important thing to do",false,false,allProjects.get(0),1,2);
            Task task5 = new Task(secret.generate(), "More Task","More tasks",false,false,allProjects.get(0),1,0);
            Task task6 = new Task(secret.generate(), "Last Task","Last task",false,false,allProjects.get(0),1,0);
            
            Task task7 = new Task(secret.generate(), "Single item","Another project",false,false,allProjects.get(1),1,0);
            Task task8 = new Task(secret.generate(), "Another single child","Some text",false,false,allProjects.get(2),1,0);


            tr.saveAll(List.of(task1, task2, task3, task4, task5, task6, task7, task8));

            List<Task> allTasks = tr.findAll();
            allTasks.get(0).setSubtasks(List.of(allTasks.get(1)));
            allTasks.get(1).setParentTask(allTasks.get(0));
            tr.save(allTasks.get(1));

            allProjects.get(0).setTasks(allTasks);
            pr.save(allProjects.get(0));

            User mainUser = ur.findById(1L).orElseThrow();
            mainUser.setProjects(allProjects);
            ur.save(mainUser);

            User petraUser = ur.findById(2L).orElseThrow();
            petraUser.addProject(allProjects.get(0));
            ur.save(petraUser);
            allProjects.get(0).addUserToProject(petraUser);

            for (Project project : allProjects) {
                project.setUsers(List.of(mainUser));
                pr.save(project);
            }

        };
    }

}
