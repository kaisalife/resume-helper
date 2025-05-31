<template>
  <div class="resume-detail-container">
    <div class="resume-paper">
      <div class="resume-header">
        <h1 class="resume-name">{{ resume.name || '未提供姓名' }}</h1>
        <p class="resume-objective">{{ resume.objective || '未提供求职意向' }}</p>
        
        <div class="contact-info">
          <div class="contact-item" v-if="resume.email">
            <span class="contact-icon">✉️</span>
            <span class="contact-text">{{ resume.email }}</span>
          </div>
          <div class="contact-item" v-if="resume.phone">
            <span class="contact-icon">📱</span>
            <span class="contact-text">{{ resume.phone }}</span>
          </div>
        </div>
      </div>

      <!-- 教育背景 -->
      <div class="resume-section" v-if="resume.education && resume.education.length > 0">
        <h2 class="section-title">教育背景</h2>
        <div class="education-container">
          <div class="education-item" v-for="(edu, index) in resume.education" :key="index">
            <div class="education-header">
              <h3 class="school-name">{{ edu.school }}</h3>
              <span class="education-time">{{ edu.time }}</span>
            </div>
            <div class="education-details">
              <p class="major">{{ edu.major }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 专业技能 -->
      <div class="resume-section" v-if="resume.skills">
        <h2 class="section-title">专业技能</h2>
        <div class="skills-content">
          {{ resume.skills }}
        </div>
      </div>

      <!-- 简历内容部分 -->
      <div v-for="(section, index) in resume.sections" :key="index" class="resume-section">
        <h2 class="section-title">{{ section.label }}</h2>
        <div class="section-content">{{ section.content }}</div>
      </div>

      <!-- 简历评分 -->
      <div class="resume-footer">
        <div class="score-badge">
          <span class="score-text">简历评分</span>
          <span class="score-value">{{ resume.resumeScore }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  resume: {
    type: Object,
    required: true
  }
});
</script>

<style scoped>
.resume-detail-container {
  max-width: 900px;
  margin: 40px auto;
  padding: 0 20px;
}

.resume-paper {
  background-color: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.resume-header {
  text-align: center;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.resume-name {
  font-size: 28px;
  color: #1f2937;
  margin: 0 0 8px;
}

.resume-objective {
  font-size: 18px;
  color: #4b5563;
  margin: 0 0 16px;
}

.contact-info {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.contact-icon {
  font-size: 16px;
}

.contact-text {
  color: #6b7280;
}

.resume-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 20px;
  color: #1f2937;
  margin: 0 0 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e7eb;
}

/* 教育背景样式 */
.education-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.education-item {
  padding-left: 16px;
  border-left: 3px solid #3b82f6;
}

.education-header {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-bottom: 8px;
}

.school-name {
  font-size: 18px;
  color: #1f2937;
  margin: 0;
}

.education-time {
  color: #6b7280;
  font-size: 14px;
}

.major {
  color: #4b5563;
  margin: 0;
}

/* 技能部分 */
.skills-content {
  white-space: pre-wrap;
  line-height: 1.6;
}

/* 简历内容部分 */
.section-content {
  white-space: pre-wrap;
  line-height: 1.6;
}

/* 底部样式 */
.resume-footer {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

.score-badge {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  padding: 10px 20px;
  border-radius: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score-text {
  font-size: 12px;
  opacity: 0.9;
}

.score-value {
  font-size: 24px;
  font-weight: 700;
}

@media (max-width: 600px) {
  .resume-paper {
    padding: 20px;
  }
  
  .education-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .education-time {
    margin-top: 4px;
  }
}
</style>    