package com.springboot.resumehelper.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String objective;
    @Column(name = "skills", columnDefinition = "TEXT") // 明确指定为TEXT
    private String skills;
    private Long userId;
    private long resumeScore;
    private String resumeTitle;
    private Boolean isShare;
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResumeSection> sections = new ArrayList<>();
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();
}

