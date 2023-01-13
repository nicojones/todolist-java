package com.minimaltodo.list.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.minimaltodo.list.project.Project;
import com.minimaltodo.list.project.ProjectIcon;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @SequenceGenerator(name = "task_sequence", sequenceName = "task_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_sequence")
    private Long id;

    private String secret;

    @Nullable
    @ManyToOne(cascade = { CascadeType.PERSIST })
    @JoinColumn(name = "task_id")
    @JsonIgnore
    private Task parentTask;

    private String name;

    private String description;

    private boolean done;

    private boolean expanded;

    @CreationTimestamp
    private Date created;

    @UpdateTimestamp
    private Date updated;

    @Transient
    @Nullable
    private Long parentId;

    @OneToMany(mappedBy = "parentTask", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Task> subtasks;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;

    @Transient
    private Long projectId;

    @Min(1)
    private int level;

    private int priority;

    @JsonProperty("projectId")
    public Long getFrontendProjectId() {
        return project.getId();
    }

    @JsonProperty("projectSecret")
    public String getFrontendProjectSecret() {
        return project.getSecret();
    }

    @JsonProperty("icon")
    public ProjectIcon getFrontendProjectIcon() {
        return project.getIcon();
    }

    @JsonProperty("dotColor")
    public String getFrontendDotColor() {
        return project.getColor();
    }

    @JsonProperty("projectName")
    public String getFrontendProjectName() {
        return project.getName();
    }

    public Task(String secret, String name, String description, boolean done, boolean expanded, Project project,
            int level, int priority) {
        this.name = name;
        this.description = description;
        this.done = done;
        this.expanded = expanded;
        this.subtasks = new ArrayList<>();
        this.project = project;
        this.level = level;
        this.priority = priority;
        this.secret = secret;
    }

    @Override
    public String toString() {
        return String.format(
                "Task: name %s // subtasks %s // parent %s // level %s // project %s",
                name,
                subtasks.size(),
                parentTask != null ? parentTask.name : "top-level",
                level,
                project.getName());
    }

}
