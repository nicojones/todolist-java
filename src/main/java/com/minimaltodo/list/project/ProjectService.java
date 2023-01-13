package com.minimaltodo.list.project;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minimaltodo.config.SecretGenerator;
import com.minimaltodo.list.task.TaskRepository;
import com.minimaltodo.list.user.User;
import com.minimaltodo.list.user.UserRepository;

@Service
public class ProjectService {

	private final ProjectRepository repository;
	private final UserRepository userRepository;
	private final TaskRepository taskRepository;

	@Autowired
	public ProjectService(
			ProjectRepository repository,
			UserRepository userRepository,
			TaskRepository taskRepository) {
		this.repository = repository;
		this.userRepository = userRepository;
		this.taskRepository = taskRepository;
	}

	public List<Project> getAllProjectsForUser(User user) {
		return repository.findAllForUser(user.getId());
	}

	public Project saveProject(User user, Project project) {
		project.addUserToProject(user);
		project.setSort(ProjectSort.Z_TO_A);
		project.setTasks(List.of());

		String unusedSecret;
		do {
			unusedSecret = new SecretGenerator().generate();
		} while (repository.findProjectBySecret(unusedSecret) != null);

		project.setSecret(unusedSecret);
		project.setIcon(ProjectIcon.CIRCLE);
		Project savedProject = repository.save(project);
		user.addProject(savedProject);
		userRepository.save(user);

		return savedProject;
	}

	public List<User> getUsersForProject(User user, Long projectId) throws AccessDeniedException {

		if (!isProjectOwner(user, projectId)) {
			throw new AccessDeniedException("current user does not own the project you want to update");
		}

		return repository.getUsersForProject(projectId);
	}

	public Project updateProject(User user, Project project) throws AccessDeniedException {

		if (!isProjectOwner(user, project.getId())) {
			throw new AccessDeniedException("current user does not own the project you want to update");
		}

		project.setUsers(List.of(user));

		Project repositoryProject = repository.findById(project.getId()).orElseThrow();
		project.setTasks(repositoryProject.getTasks());

		System.out.println("potato");
		System.out.println(project.getIcon());
		// project.setIcon(ProjectIcon.getEnumByString(project.getIcon().label));

		return repository.save(project);
	}

	public void deleteProject(User user, Long projectId) throws AccessDeniedException {

		if (!isProjectOwner(user, projectId)) {
			throw new AccessDeniedException("current user does not own the project you want to delete");
		}

		Project project = repository.findById(projectId).orElseThrow();
		taskRepository.deleteAll(project.getTasks());

		project.setTasks(new ArrayList<>());

		user.removeProject(project);
		userRepository.save(user);

		repository.delete(project);
	}

	public void addUserToProject(User user, Long projectId, String newUserEmail) throws AccessDeniedException {

		if (!isProjectOwner(user, projectId)) {
			throw new AccessDeniedException("current user does not own the project you want to share");
		}

		Project project = repository.findById(projectId).orElseThrow();

		User newUser = userRepository.findUserByEmail(newUserEmail).orElseThrow();
		newUser.addProject(project);

		userRepository.save(newUser);
	}

	public void removeUserFromProject(User user, Long projectId, String userToRemoveEmail) throws AccessDeniedException {

		if (!isProjectOwner(user, projectId)) {
			throw new AccessDeniedException("current user does not have rights to remove other users from project");
		}

		Project project = repository.findById(projectId).orElseThrow();

		User userToRemove = userRepository.findUserByEmail(userToRemoveEmail).orElseThrow();
		userToRemove.removeProject(project);

		userRepository.save(userToRemove);
	}

	public void deleteAllTasks(User user, Long projectId) throws AccessDeniedException {
		if (!isProjectOwner(user, projectId)) {
			throw new AccessDeniedException("current user does not have rights to remove other users from project");
		}

		Project project = repository.findById(projectId).orElseThrow();

		taskRepository.deleteAll(project.getTasks());

		project.setTasks(new ArrayList<>());
		repository.save(project);

	}

	public boolean isProjectOwner(User loggedInUser, Long projectId) {

		return loggedInUser
				.getProjects()
				.stream()
				.anyMatch(it -> {
					System.out.println(projectId);
					return it.getId().equals(projectId);
				});
	}

}
