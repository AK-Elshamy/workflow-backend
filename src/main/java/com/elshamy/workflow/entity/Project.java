package com.elshamy.workflow.entity;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;



    public Project() {
    }
    public Project(String name, String description) {
        this.name = name;
        this.description = description;
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public User getOwner() {
        return owner;
    }
    public Set<User> getMembers() {
        return members;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }


    @ManyToMany
    @JoinTable(
            name = "project_members",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    public void addMember(User user){
        members.add(user);
        user.addMemberProject(this);
    }

    @OneToMany(mappedBy = "project")
    private Set<Task> tasks = new HashSet<>();

    public void addTask(Task task){
        tasks.add(task);
        task.setProject(this);
    }

    public Set<Task> getTasks() {
        return tasks;
    }


}