package com.springboot.resumehelper.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@Entity
@Table(name = "education")
public class Education {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String school;     // 学校名称
    
    @Column(nullable = false)
    private String major;      // 专业及学位
    
    @Column(nullable = false)
    private String time;       // 时间范围，例如"2018-09 至 2022-07"，与前端保持一致
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;     // 关联的简历
} 