package com.minimaltodo.list.task;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.minimaltodo.config.SecretGenerator;
import com.minimaltodo.list.project.Project;
import com.minimaltodo.list.project.ProjectRepository;
import com.minimaltodo.list.project.ProjectSort;
import com.minimaltodo.list.user.User;

@Service
public class TaskService {

	private final TaskRepository repository;
	private final ProjectRepository projectRepository;

	@Autowired
	public TaskService(TaskRepository repository, ProjectRepository projectRepository) {
		this.repository = repository;
		this.projectRepository = projectRepository;
	}

	public List<Task> getIncompleteTasks(User user) throws AccessDeniedException {

		List<Long> projectIds = user.getProjects().stream().map(it -> it.getId()).collect(Collectors.toList());
		List<Task> tasks = repository.findIncompleteTasks(projectIds);

		return tasks;
	}

	public List<Task> getPriorityTasks(User user) throws AccessDeniedException {

		List<Long> projectIds = user.getProjects().stream().map(it -> it.getId()).collect(Collectors.toList());
		List<Task> tasks = repository.findPriorityTasks(projectIds);

		return tasks;
	}

	public List<Task> getAllTasksForProject(User user, String projectSecret) throws AccessDeniedException {

		Project project = projectRepository.findProjectBySecret(projectSecret);

		validateUserIsProjectOwner(user, project.getId(), "user does not own the Project");

		List<Task> tasks = repository.findTaskByProject(project, getSort(project.getSort()));

		return tasks;
	}

	public Task saveTask(Task task, User user, Long projectId) throws AccessDeniedException {

		validateUserIsProjectOwner(user, projectId, "user does not own the Project");

		Project project = projectRepository.findById(projectId).orElseThrow();
		Long taskParentId = task.getParentId();
		task.setProject(project);

		String unusedSecret;
		do {
			unusedSecret = new SecretGenerator().generate();
		} while (repository.findTaskBySecret(unusedSecret) != null);

		task.setSecret(unusedSecret);
		
		repository.save(task);
		Task savedTask = repository.findById(task.getId()).orElseThrow();

		if (taskParentId != null) {
			Task parentTask = repository.findById(taskParentId).orElseThrow();

			savedTask.setLevel(parentTask.getLevel() + 1);
			savedTask.setParentTask(parentTask);
		}

		return repository.save(savedTask);
	}

	public Task updateTask(Task task, User user, Long projectId) throws AccessDeniedException {

		validateUserIsProjectOwner(user, projectId, "user does not own the Project");

		Task savedTask = repository.findById(task.getId()).orElseThrow();
		Project project = projectRepository.findById(projectId).orElseThrow();

		populateSubtasks(List.of(task), project);
		task.setLevel(savedTask.getLevel());

		return repository.save(task);
	}

	public Task getTaskById(Long taskId, User user) throws AccessDeniedException {
		Task task = repository.findById(taskId).orElseThrow();

		validateUserIsProjectOwner(user, task.getProject().getId(), "user does not own the task");
		return task;
	}

	public Task toggleTask(Task task, User user) throws AccessDeniedException {
		task.setDone(!task.isDone());
		return saveTask(task, user, task.getProject().getId());
	}

	public List<Task> toggleSubtasks(List<Task> tasks, boolean nextToggleState) {
		tasks
				.stream()
				.forEach(it -> {
					it.setDone(nextToggleState);
				});
		return repository.saveAll(tasks);
	}

	public List<Task> searchTasks(User u, String query) {
		return repository.searchAllTasks(u.getId(), query);
	}

	public void deleteTask(Task task) {
		repository.delete(task);
	}

	public void validateUserIsProjectOwner(User loggedInUser, Long projectId, String error) throws AccessDeniedException {

		boolean isOwner = loggedInUser
				.getProjects()
				.stream()
				.anyMatch(it -> it.getId().equals(projectId));

		if (isOwner == false) {
			throw new AccessDeniedException(error);
		}
	}

	public void populateSubtasks(List<Task> tasks, Project project) {
		tasks.stream().forEach(it -> {
			populateSubtasks(it.getSubtasks(), project);
			it.setProject(project);
		});
	}

	public Sort getSort(ProjectSort projectSort) {

		switch (projectSort) {
			case PRIORITY:
				return Sort.by("priority").descending().and(Sort.by("created").ascending());
			case A_TO_Z:
				return Sort.by("name", "created").ascending();
			case Z_TO_A:
				return Sort.by("name", "created").descending();
			case OLDEST_FIRST:
				return Sort.by("created").ascending();
			case NEWEST_FIRST:
				return Sort.by("created").descending();
			default:
				return Sort.by("created").ascending();
		}
	}

}
