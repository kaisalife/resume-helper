package com.springboot.resumehelper.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@Entity
@Table(name = "resume_section") // 明确指定数据库表名
public class ResumeSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String label;
    @Column(name = "content", columnDefinition = "TEXT") // 明确指定为TEXT
    private String content;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id") // 只保留这一个映射
    private Resume resume;

    // 移除 @Column 注解的 resumeId 字段
}