package com.elshamy.workflow.entity;


import com.elshamy.workflow.enums.Role;
import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users")

public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    public Set<Project> getMemberProjects() {
        return memberProjects;
    }
    public void setMemberProjects(Set<Project> memberProjects) {
        this.memberProjects = memberProjects;
    }


    @OneToMany(mappedBy = "owner")
    private Set<Project> ownedProjects = new HashSet<>();


    @ManyToMany(mappedBy = "members")
    private Set<Project> memberProjects = new HashSet<>();

    @OneToMany(mappedBy = "assignee")
    private Set<Task> assignedTasks = new HashSet<>();

    @OneToMany(mappedBy = "author")
    private List<Comment> comments = new ArrayList<>();

    public void addComment(Comment comment){
        comments.add(comment);
        comment.setAuthor(this);
    }

    public User() {}
    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void addProject(Project project){
        ownedProjects.add(project);
        project.setOwner(this);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addMemberProject(Project project){
        memberProjects.add(project);
    }

    public void addTask(Task task){
        assignedTasks.add(task);
        task.setAssignee(this);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public Role getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<Project> getOwnedProjects() {
        return ownedProjects;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
