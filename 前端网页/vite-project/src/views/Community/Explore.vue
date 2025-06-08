<template>
  <div class="resume-community">
    <div class="community-header">
      <h1 class="page-title">简历社区</h1>
      <p class="subtitle">探索优秀简历，获取灵感与参考</p>
      
      <!-- 搜索框 -->
      <div class="search-container">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="搜索求职方向、技能或学校..."
          class="search-input"
        />
        <button class="search-btn">
          <span class="search-icon">🔍</span>
        </button>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <div class="spinner"></div>
      <p>正在加载简历数据...</p>
    </div>
    
    <!-- 错误提示 -->
    <div v-if="errorMessage" class="error-container">
      <div class="error-icon">⚠️</div>
      <p>{{ errorMessage }}</p>
      <button @click="fetchResumes" class="retry-btn">重新加载</button>
    </div>
    
    <!-- 没有结果 -->
    <div v-if="!isLoading && !errorMessage && filteredResumes.length === 0" class="no-results">
      <div class="no-results-icon">📄</div>
      <p>没有找到匹配的简历</p>
      <p class="hint">尝试使用不同的搜索关键词，或查看所有简历</p>
      <button @click="searchQuery = ''" class="clear-search-btn">清除搜索</button>
    </div>
    
    <!-- 简历列表 -->
    <div class="resume-list" v-if="!isLoading && !errorMessage && filteredResumes.length > 0">
      <div v-for="resume in filteredResumes" :key="resume.id" class="resume-card">
        <div class="resume-card-header">
          <div class="resume-score-badge">{{ resume.resumeScore }}分</div>
          <h3 class="resume-title">{{ resume.resumeTitle || '无标题简历' }}</h3>
        </div>
        
        <div class="resume-card-body">
          <div class="resume-info-row">
            <span class="info-label">求职方向:</span>
            <span class="info-value highlight">{{ resume.objective || '未指定' }}</span>
          </div>
          
          <!-- 教育背景 -->
          <div class="education-info" v-if="resume.education && resume.education.length > 0">
            <div class="education-item" v-for="(edu, index) in resume.education.slice(0, 1)" :key="index">
              <span class="school">{{ edu.school }}</span>
              <span class="major">{{ edu.major }}</span>
            </div>
            <div class="more-edu" v-if="resume.education.length > 1">
              +{{ resume.education.length - 1 }} 其他教育经历
            </div>
          </div>
          <div class="no-education" v-else>暂无教育背景信息</div>
          
          <!-- 技能标签 -->
          <div class="skills-container" v-if="resume.skills">
            <div class="skills-tags">
              <span 
                v-for="(skill, index) in getSkillTags(resume.skills)" 
                :key="index" 
                class="skill-tag"
              >
                {{ skill }}
              </span>
            </div>
          </div>
        </div>
        
        <div class="resume-card-footer">
          <button class="view-btn" @click="showResumeDetails(resume)">
            查看详情
          </button>
        </div>
      </div>
    </div>
    
    <!-- 简历详情弹窗 -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2 class="modal-title">{{ selectedResume.resumeTitle || '简历详情' }}</h2>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        
        <div class="modal-body">
          <div class="detail-section">
            <h3 class="section-title">基本信息</h3>
            <div class="detail-grid">
              <div class="detail-item">
                <span class="detail-label">姓名:</span>
                <span class="detail-value">{{ selectedResume.name || '未提供' }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">求职方向:</span>
                <span class="detail-value">{{ selectedResume.objective || '未提供' }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">邮箱:</span>
                <span class="detail-value">{{ selectedResume.email || '未提供' }}</span>
              </div>
              <div class="detail-item">
                <span class="detail-label">电话:</span>
                <span class="detail-value">{{ selectedResume.phone || '未提供' }}</span>
              </div>
            </div>
          </div>
          
          <!-- 教育背景 -->
          <div class="detail-section" v-if="selectedResume.education && selectedResume.education.length > 0">
            <h3 class="section-title">教育背景</h3>
            <div class="education-list">
              <div class="education-detail" v-for="(edu, index) in selectedResume.education" :key="index">
                <div class="education-header">
                  <div class="education-primary">
                    <span class="school-name">{{ edu.school }}</span>
                    <span class="education-time">{{ edu.time }}</span>
                  </div>
                  <div class="education-major">{{ edu.major }}</div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 技能 -->
          <div class="detail-section" v-if="selectedResume.skills">
            <h3 class="section-title">专业技能</h3>
            <p class="skills-content">{{ selectedResume.skills }}</p>
            <div class="skills-tags detail-tags">
              <span 
                v-for="(skill, index) in getSkillTags(selectedResume.skills)" 
                :key="index" 
                class="skill-tag"
              >
                {{ skill }}
              </span>
            </div>
          </div>
          
          <!-- 简历内容部分 -->
          <div class="detail-section" v-if="selectedResume.sections && selectedResume.sections.length > 0">
            <h3 class="section-title">简历内容</h3>
            <div v-for="(section, index) in selectedResume.sections" :key="index" class="resume-section">
              <h4 class="resume-section-title">{{ section.label }}</h4>
              <div class="resume-section-content">{{ section.content }}</div>
            </div>
          </div>
          
          <div class="resume-score-container">
            <div class="score-display">
              <span class="score-label">简历评分</span>
              <span class="score-value">{{ selectedResume.resumeScore }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { getSharedResumes } from '../../api/social.js'; // 导入API函数

// 状态管理
const sharedResumes = ref([]);
const searchQuery = ref('');
const filteredResumes = computed(() => {
  if (!searchQuery.value.trim()) return sharedResumes.value;
  
  const query = searchQuery.value.toLowerCase();
  return sharedResumes.value.filter(resume => {
    // 搜索简历的多个字段
    const matchesObjective = resume.objective && resume.objective.toLowerCase().includes(query);
    const matchesSkills = resume.skills && resume.skills.toLowerCase().includes(query);
    const matchesEducation = resume.education && resume.education.some(edu => 
      (edu.school && edu.school.toLowerCase().includes(query)) || 
      (edu.major && edu.major.toLowerCase().includes(query))
    );
    
    return matchesObjective || matchesSkills || matchesEducation;
  });
});

const showModal = ref(false);
const selectedResume = ref({});
const isLoading = ref(true);
const errorMessage = ref('');

// 生命周期钩子：组件挂载后立即获取数据
onMounted(async () => {
  await fetchResumes();
});

// 提取为可复用的获取数据函数
const fetchResumes = async () => {
  isLoading.value = true;
  errorMessage.value = '';
  
  try {
    const response = await getSharedResumes();
    sharedResumes.value = response;
  } catch (error) {
    console.error('获取分享的简历时出错:', error);
    errorMessage.value = '获取简历数据失败，请稍后重试';
  } finally {
    isLoading.value = false;
  }
};

const showResumeDetails = (resume) => {
  selectedResume.value = resume;
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
};

// 从技能字符串中提取标签
const getSkillTags = (skillsString) => {
  if (!skillsString) return [];
  
  // 尝试按不同的分隔符拆分技能
  let skills = [];
  if (skillsString.includes('、')) {
    skills = skillsString.split('、');
  } else if (skillsString.includes(',')) {
    skills = skillsString.split(',');
  } else if (skillsString.includes('，')) {
    skills = skillsString.split('，');
  } else if (skillsString.includes(';')) {
    skills = skillsString.split(';');
  } else if (skillsString.includes('；')) {
    skills = skillsString.split('；');
  } else {
    // 如果没有明确的分隔符，可能是一个整段文字，只显示前几个词
    skills = [skillsString.substring(0, 15) + (skillsString.length > 15 ? '...' : '')];
  }
  
  // 过滤掉空字符串并限制数量
  return skills.filter(s => s.trim()).slice(0, 4);
};
</script>

<style scoped>
.resume-community {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.community-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 32px;
  color: #2c3e50;
  margin: 0 0 8px;
}

.subtitle {
  color: #7f8c8d;
  font-size: 16px;
  margin-bottom: 24px;
}

.search-container {
  display: flex;
  max-width: 600px;
  margin: 0 auto;
  border-radius: 30px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e0e0e0;
}

.search-input {
  flex: 1;
  padding: 14px 20px;
  font-size: 16px;
  border: none;
  outline: none;
}

.search-btn {
  background: linear-gradient(to right, #3b82f6, #2563eb);
  color: white;
  border: none;
  padding: 0 24px;
  cursor: pointer;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-left-color: #3b82f6;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 错误提示 */
.error-container {
  background-color: #fee2e2;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  margin: 40px 0;
  color: #b91c1c;
}

.error-icon {
  font-size: 36px;
  margin-bottom: 16px;
}

.retry-btn {
  background-color: #ef4444;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  margin-top: 16px;
  transition: background-color 0.2s;
}

.retry-btn:hover {
  background-color: #dc2626;
}

/* 没有结果 */
.no-results {
  text-align: center;
  padding: 60px 0;
  color: #6b7280;
}

.no-results-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.hint {
  margin-bottom: 24px;
  font-size: 14px;
  color: #9ca3af;
}

.clear-search-btn {
  background-color: #f3f4f6;
  color: #4b5563;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.clear-search-btn:hover {
  background-color: #e5e7eb;
}

/* 简历列表 */
.resume-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.resume-card {
  border-radius: 12px;
  overflow: hidden;
  background-color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  display: flex;
  flex-direction: column;
  border: 1px solid #e5e7eb;
}

.resume-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.1);
}

.resume-card-header {
  padding: 16px 20px;
  background-color: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  position: relative;
}

.resume-score-badge {
  position: absolute;
  top: 16px;
  right: 20px;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  border-radius: 20px;
  padding: 4px 12px;
  font-size: 14px;
  font-weight: 600;
}

.resume-title {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
  padding-right: 60px; /* 为分数腾出空间 */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.resume-card-body {
  padding: 16px 20px;
  flex: 1;
}

.resume-info-row {
  margin-bottom: 12px;
}

.info-label {
  color: #6b7280;
  font-size: 14px;
  margin-right: 8px;
}

.info-value {
  color: #1f2937;
  font-size: 14px;
}

.info-value.highlight {
  font-weight: 600;
  color: #2563eb;
}

/* 教育背景样式 */
.education-info {
  margin-bottom: 16px;
}

.education-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 4px;
}

.school {
  font-weight: 600;
  color: #1f2937;
  font-size: 15px;
}

.major {
  color: #4b5563;
  font-size: 14px;
}

.more-edu {
  font-size: 13px;
  color: #6b7280;
  font-style: italic;
}

.no-education {
  font-size: 14px;
  color: #9ca3af;
  font-style: italic;
  margin-bottom: 16px;
}

/* 技能标签 */
.skills-container {
  margin-top: 12px;
}

.skills-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.skill-tag {
  background-color: #f3f4f6;
  color: #4b5563;
  padding: 4px 10px;
  border-radius: 16px;
  font-size: 12px;
  display: inline-block;
}

.resume-card-footer {
  padding: 12px 20px;
  border-top: 1px solid #e5e7eb;
  background-color: #f9fafb;
}

.view-btn {
  width: 100%;
  background: linear-gradient(to right, #3b82f6, #2563eb);
  color: white;
  border: none;
  border-radius: 6px;
  padding: 10px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.view-btn:hover {
  background: linear-gradient(to right, #2563eb, #1d4ed8);
  transform: translateY(-2px);
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background-color: white;
  border-radius: 12px;
  max-width: 800px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.modal-header {
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background-color: white;
  z-index: 1;
}

.modal-title {
  margin: 0;
  font-size: 22px;
  color: #1f2937;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #6b7280;
  cursor: pointer;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: #f3f4f6;
}

.modal-body {
  padding: 24px;
}

.detail-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e5e7eb;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
}

.detail-label {
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 4px;
}

.detail-value {
  color: #1f2937;
  font-size: 16px;
}

/* 教育背景详情 */
.education-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.education-detail {
  border-left: 3px solid #3b82f6;
  padding-left: 16px;
}

.education-header {
  margin-bottom: 8px;
}

.education-primary {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.school-name {
  font-weight: 600;
  font-size: 16px;
  color: #1f2937;
}

.education-time {
  color: #6b7280;
  font-size: 14px;
}

.education-major {
  color: #4b5563;
}

/* 技能详情 */
.skills-content {
  margin-bottom: 16px;
  white-space: pre-wrap;
  line-height: 1.6;
}

.detail-tags {
  margin-bottom: 8px;
}

/* 简历章节 */
.resume-section {
  margin-bottom: 20px;
}

.resume-section-title {
  font-size: 16px;
  color: #374151;
  margin: 0 0 10px;
}

.resume-section-content {
  white-space: pre-wrap;
  line-height: 1.6;
}

/* 分数展示 */
.resume-score-container {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: center;
}

.score-display {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  padding: 16px 40px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 10px 15px -3px rgba(59, 130, 246, 0.3);
}

.score-label {
  font-size: 14px;
  margin-bottom: 4px;
  opacity: 0.9;
}

.score-value {
  font-size: 32px;
  font-weight: 700;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .resume-list {
    grid-template-columns: 1fr;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>