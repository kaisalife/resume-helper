-- 创建教育背景表
CREATE TABLE `education` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `school` varchar(255) NOT NULL COMMENT '学校名称',
  `major` varchar(255) NOT NULL COMMENT '专业及学位',
  `time` varchar(100) NOT NULL COMMENT '时间范围',
  `resume_id` bigint NOT NULL COMMENT '关联的简历ID',
  PRIMARY KEY (`id`),
  KEY `idx_resume_id` (`resume_id`),
  CONSTRAINT `fk_education_resume` FOREIGN KEY (`resume_id`) REFERENCES `resume` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='教育背景表';

-- 修改ResumeGenRequest类增加教育背景支持
-- 可以在ResumeGenRequest.java中添加类似代码:
-- 
-- // 教育背景部分内部类
-- @Data
-- public static class Education {
--     private String school;     // 学校
--     private String major;      // 专业及学位
--     private String time;       // 时间
-- }
-- 
-- private List<Education> education = new ArrayList<>(); // 教育背景列表，与前端一致 