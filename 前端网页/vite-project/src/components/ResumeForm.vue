<template>
  <div class="resume-form-container">
    <div class="resume-header">
      <h1 class="main-title">简历编辑器</h1>
      <div class="header-actions">
        <button @click="showResumeListModal = true" class="primary-btn">
          <i class="icon">📋</i> 查看已存储的简历
        </button>
        <button @click="navigateToCommunity" class="community-btn">
          <i class="icon">🌐</i> 简历社区
        </button>
        <div class="resume-score-badge" v-show="resume.resumeScore > 0">
          <span class="score-label">简历分数</span>
          <span class="score-value">{{ resume.resumeScore }}</span>
        </div>
      </div>
    </div>

    <!-- 简历列表弹窗 -->
    <div v-if="showResumeListModal" class="modal-overlay">
      <div class="modal">
        <div class="modal-header">
          <h2>已存储的简历列表</h2>
          <button @click="showResumeListModal = false" class="icon-btn close-icon">×</button>
        </div>
        <div class="modal-content">
          <ul class="resume-list">
            <li 
              v-for="(resumeItem, index) in resumes"  
              :key="resumeItem.id" 
              @click="selectResume(resumeItem)"
              class="resume-item"
            >
              <div class="resume-info">
                <span class="title">{{ resumeItem.resumeTitle }}</span>
                <span class="score">{{ resumeItem.resumeScore }} 分</span>
              </div>
              <div class="resume-actions">
                <button
                  @click.stop="handleShareResume(resumeItem)"
                  :disabled="resumeItem.resumeScore < 90"
                  :class="['share-btn', resumeItem.isShare ? 'shared' : '']"
                >
                  {{ resumeItem.isShare ? '取消分享' : '分享到社区' }}
                </button>
                <button
                  @click.stop="handleDeleteResume(resumeItem.resumeTitle)"
                  class="delete-btn"
                >
                  删除
                </button>
              </div>
            </li>
          </ul>
        </div>
        <div class="modal-footer">
          <button @click="showResumeListModal = false" class="secondary-btn">关闭</button>
        </div>
      </div>
    </div>

    <!-- 简历编辑区域 -->
    <div class="content-wrapper">
      <div class="form-container">
        <h2 class="section-title">简历编辑</h2>
        <form @submit.prevent="handleSaveResume" class="resume-edit-form">
          <!-- 简历标题 -->
          <div class="form-group">
            <label for="resumeTitle">简历标题</label>
            <input
              type="text"
              id="resumeTitle"
              v-model="resume.resumeTitle"
              placeholder="请输入简历标题"
              class="form-control"
            >
          </div>

          <!-- 个人信息区块 -->
          <div class="info-section">
            <h3 class="subsection-title">个人信息</h3>
            <div class="form-row">
              <div class="form-group">
                <label for="name">姓名</label>
                <input type="text" id="name" v-model="resume.name" placeholder="请输入姓名">
              </div>
              <div class="form-group">
                <label for="phone">电话</label>
                <input type="tel" id="phone" v-model="resume.phone" placeholder="请输入电话">
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label for="email">邮箱</label>
                <input type="email" id="email" v-model="resume.email" placeholder="请输入邮箱">
              </div>
              <div class="form-group">
                <label for="objective">求职意向</label>
                <input type="text" id="objective" v-model="resume.objective" placeholder="请输入求职意向">
              </div>
            </div>
          </div>

          <!-- 教育背景区块 -->
          <div class="education-section">
            <h3 class="subsection-title">教育背景</h3>
            
            <!-- 已添加的教育经历列表 -->
            <div v-for="(edu, index) in resume.education" :key="index" class="education-item">
              <div class="education-header">
                <h4 class="education-school">{{ edu.school }}</h4>
                <div class="education-controls">
                  <button type="button" @click="removeEducation(index)" class="icon-btn delete-btn">
                    <span>🗑️</span>
                  </button>
                </div>
              </div>
              <div class="education-details">
                <div class="education-major">
                  <span class="label">专业:</span> {{ edu.major }}
                </div>
                <div class="education-time">
                  <span class="label">时间:</span> {{ edu.time }}
                </div>
              </div>
              <div class="education-actions">
                <button type="button" @click="editEducation(index)" class="action-btn">
                  编辑
                </button>
              </div>
            </div>

            <!-- 添加教育经历表单 -->
            <div class="add-education-form" v-if="showEducationForm">
              <div class="form-row">
                <div class="form-group">
                  <label for="eduSchool">攻读学校及其学位</label>
                  <input type="text" id="eduSchool" v-model="newEducation.school" placeholder="例如：北京大学-985+本科/清华大学-985+硕士">
                </div>
                <div class="form-group">
                  <label for="eduMajor">攻读专业</label>
                  <input type="text" id="eduMajor" v-model="newEducation.major" placeholder="请输入专业名称">
                </div>
              </div>
              <div class="form-group">
                <label for="eduTime">就读时间</label>
                <input type="text" id="eduTime" v-model="newEducation.time" placeholder="例如: 2018年9月 - 2022年6月">
              </div>
              <div class="education-form-actions">
                <button type="button" @click="saveEducation" class="action-btn save-btn">
                  {{ editingEducationIndex > -1 ? '保存修改' : '添加' }}
                </button>
                <button type="button" @click="cancelEducationForm" class="action-btn cancel-btn">
                  取消
                </button>
              </div>
            </div>

            <!-- 添加教育经历按钮 -->
            <button 
              type="button" 
              @click="showAddEducationForm" 
              class="add-education-btn"
              v-if="!showEducationForm"
            >
              <span class="btn-icon">➕</span> 添加教育经历
            </button>
          </div>

          <!-- 简历模块编辑区块 -->
          <div class="sections-container">
            <h3 class="subsection-title">简历模块</h3>
            <div v-for="(section, index) in resume.sections" :key="index" class="section-card">
              <div class="section-header">
                <label class="section-label">{{ section.label }}</label>
                <div class="section-controls">
                  <button type="button" @click="removeSection(index)" class="icon-btn delete-btn">
                    <span>🗑️</span>
                  </button>
                </div>
              </div>
              <textarea v-model="section.content" placeholder="请输入内容" class="section-content"></textarea>
              <div class="section-actions">
                <button type="button" @click="addSectionBefore(index)" class="action-btn">在上方添加</button>
                <button type="button" @click="addSectionAfter(index)" class="action-btn">在下方添加</button>
              </div>
            </div>

            <!-- 添加自定义模块 -->
            <div class="add-section-container">
              <input 
                type="text" 
                v-model="newSectionLabel" 
                placeholder="新模块名称" 
                class="add-section-input"
              />
              <button 
                type="button"
                @click="addCustomSection(resume.sections.length)" 
                class="add-section-btn"
                :disabled="!newSectionLabel"
              >
                添加自定义模块
              </button>
            </div>
          </div>

          <!-- 技能字段 -->
          <div class="form-group skills-group">
            <label for="skills">专业技能</label>
            <textarea id="skills" v-model="resume.skills" placeholder="请输入专业技能" class="skills-textarea"></textarea>
          </div>

          <!-- 操作按钮组 -->
          <div class="form-actions">
            <button type="submit" class="primary-btn">保存简历</button>
            <button type="button" @click="generateAndDownloadResume" class="secondary-btn">
              生成并下载简历
            </button>
            <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
            <div v-if="successMessage" class="success-message">{{ successMessage }}</div>
          </div>
        </form>
      </div>

      <!-- AI功能模块 -->
      <div class="ai-features">
        <h2 class="section-title">AI 智能助手</h2>
        <div class="ai-buttons">
          <button @click="checkResume" class="ai-btn check-btn">
            <span class="ai-icon">🔍</span>
            <span>语法检查</span>
          </button>
          <button @click="simulatePassRate" class="ai-btn simulate-btn">
            <span class="ai-icon">📊</span>
            <span>模拟筛选</span>
          </button>
        </div>

        <!-- AI检查结果 -->
        <div class="ai-result-container" v-show="aiCheckResult.problems || aiCheckResult.suggestion">
          <h3 class="ai-result-title">简历语法与格式检查</h3>

          <!-- 问题列表 -->
          <div class="ai-section">
            <h4 class="section-title">
              <span class="icon">⚠️</span> 检测到的问题
            </h4>
            <div v-if="aiCheckResult.problems">
              <ul class="problem-list">
                <li v-for="(problem, index) in aiCheckResult.problems.split(';')" :key="index">
                  {{ problem.trim() }}
                </li>
              </ul>
            </div>
            <p v-else class="empty-tip">未发现语法/逻辑问题</p>
          </div>

          <!-- 建议列表 -->
          <div class="ai-section">
            <h4 class="section-title">
              <span class="icon">💡</span> 优化建议
            </h4>
            <div v-if="aiCheckResult.suggestion">
              <ul class="suggestion-list">
                <li v-for="(suggestion, index) in aiCheckResult.suggestion.split(';')" :key="index">
                  {{ suggestion.trim() }}
                </li>
              </ul>
            </div>
            <p v-else class="empty-tip">无具体优化建议</p>
          </div>
        </div>

        <!-- 模拟筛选结果 -->
        <div v-if="aiSimulateResult.score > 0" class="simulate-result">
          <h3 class="ai-result-title">简历筛选报告</h3>
          <div class="score-card">
            <div class="score-meter">
              <div class="score-value">
                {{ aiSimulateResult.score }}
                <span class="score-unit">分</span>
              </div>
              <div class="pass-status" :class="{ 'passed': aiSimulateResult.passed, 'failed': !aiSimulateResult.passed }">
                {{ aiSimulateResult.passed ? '通过筛选' : '未通过筛选' }}
              </div>
            </div>
          </div>

          <div class="result-details">
            <div class="result-section">
              <h4 class="result-section-title">综合评价</h4>
              <p class="result-text">{{ aiSimulateResult.evaluation }}</p>
            </div>

            <div class="result-section">
              <h4 class="result-section-title">改进建议</h4>
              <p class="result-text" v-if="aiSimulateResult.improvement">{{ aiSimulateResult.improvement }}</p>
              <p class="result-text empty" v-else>无明显改进点</p>
            </div>

            <div class="result-section interview-info">
              <h4 class="result-section-title">面试安排</h4>
              <div class="interview-details">
                <p><strong>面试官：</strong> {{ aiSimulateResult.interviewer || '系统自动评估' }}</p>
                <p><strong>面试类型：</strong> {{ aiSimulateResult.interviewType || '待定' }}</p>
              </div>
            </div>

            <div class="result-section" v-if="aiSimulateResult.analysis">
              <h4 class="result-section-title">详细分析</h4>
              <pre class="analysis-code">{{ aiSimulateResult.analysis }}</pre>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { aiCheckResume, aiSimulatePassRate } from '../api/ai.js';
import { saveResume, generateResume, getResumes, updateResumeShareStatus, deleteResume } from '../api/resume.js';
import { useRouter } from 'vue-router';

const router = useRouter();

const aiCheckResult = ref({
  problems: '', // 问题列表（分号分隔）
  suggestion: '' // 建议列表（分号分隔）
});
// 原有变量（完全保留）
const errorMessage = ref('');
const successMessage = ref('');
const isGenerating = ref(false);
const resumes = ref([]);
const resume = ref({
  name: '',
  email: '',
  phone: '',
  objective: '',
  sections: [],
  skills: '',
  resumeTitle: '',
  resumeScore: 0,
  isShare: false,
  education: [] // 新增：教育背景列表
});
const newSectionLabel = ref('');
const showResumeListModal = ref(false);

// AI相关变量
const aiResult = ref('');
const aiSimulateResult = ref({
  resumeId: null,
  passed: false,
  score: -1,
  evaluation: "",
  improvement: "",
  interviewer: "",
  interviewType: "",
  transcript: "",
  analysis: ""
});

// 教育背景相关变量
const showEducationForm = ref(false);
const editingEducationIndex = ref(-1);
const newEducation = ref({
  school: '',
  major: '',
  time: ''
});

// AI语法检查
const checkResume = async () => {
  try {
    // 调用后端接口获取检查结果（假设返回 AICheckResult 对象）
    const result = await aiCheckResume(resume.value);
    
    // 解析问题和建议（假设后端返回的 problems/suggestion 是分号分隔的字符串）
    aiCheckResult.value.problems = result.problems;
    aiCheckResult.value.suggestion = result.suggestion;
    
    // 显示结果（设置 aiResult 为 true 触发视图更新）
    aiResult.value = true; // 用于控制结果区域显示
    
  } catch (error) {
    console.error('AI检查简历时出错:', error);
    aiResult.value = '检查失败，请重试';
    aiCheckResult.value.problems = '';
    aiCheckResult.value.suggestion = '';
  }
};

const simulatePassRate = async () => {
  try {
    const result = await aiSimulatePassRate(resume.value);
    aiSimulateResult.value = { ...result }; // 直接解构赋值，无需 .value
  } catch (error) {
    console.error('AI模拟筛选通过率时出错:', error);
    aiSimulateResult.value = {
      ...aiSimulateResult.value,
      score: 0,
      passed: false,
      evaluation: "分析失败，请检查简历内容"
    };
  }
};

// 原有方法（完全保留，不做任何修改）
onMounted(() => {
  fetchResumes();
});

const fetchResumes = async () => {
  try {
    const response = await getResumes();
    resumes.value = response.data;
  } catch (error) {
    console.error('获取简历列表时出错:', error);
  }
};

const selectResume = (resumeItem) => {
  resume.value = {
    ...resumeItem,
    sections: [...resumeItem.sections],
    education: resumeItem.education ? [...resumeItem.education] : []
  };
  showResumeListModal.value = false;
};

const handleSaveResume = async () => {
  try {
    if(aiSimulateResult.value.score != -1) {
      resume.value.resumeScore = aiSimulateResult.value.score;
    }
    const response = await saveResume(resume.value);
    console.log('简历保存成功:', response);
    fetchResumes();
  } catch (error) {
    console.error('保存简历时出错:', error);
  }
};

const handleShareResume = async (resumeItem) => {
  if (resumeItem.resumeScore < 70) {
    alert('分享需简历分数≥90分，请提升分数后重试！');
    return;
  }

  try {
    await updateResumeShareStatus(resumeItem.resumeTitle, !resumeItem.isShare);
    resumeItem.isShare = !resumeItem.isShare;
    successMessage.value = '分享状态更新成功！';
  } catch (error) {
    console.error('更新分享状态失败:', error);
    errorMessage.value = '操作失败，请重试！';
  }
};

const handleDeleteResume = async (resumeTitle) => {
  if (confirm('确定要删除这条简历吗？')) {
    try {
      await deleteResume(resumeTitle);
      fetchResumes();
    } catch (error) {
      console.error('删除简历时出错:', error);
    }
  }
};

const addSectionBefore = (index) => {
  const newSection = {
    label: newSectionLabel.value,
    content: ''
  };
  resume.value.sections.splice(index, 0, newSection);
  newSectionLabel.value = '';
};

const addSectionAfter = (index) => {
  const newSection = {
    label: newSectionLabel.value,
    content: ''
  };
  resume.value.sections.splice(index + 1, 0, newSection);
  newSectionLabel.value = '';
};

const addCustomSection = (index) => {
  if (newSectionLabel.value) {
    const newSection = {
      label: newSectionLabel.value,
      content: ''
    };
    resume.value.sections.splice(index, 0, newSection);
    newSectionLabel.value = '';
  }
};

const removeSection = (index) => {
  resume.value.sections.splice(index, 1);
};

const generateAndDownloadResume = async () => {
  try {
    isGenerating.value = true;
    errorMessage.value = '';

    const pdfData = await generateResume(resume.value);

    if (!pdfData || pdfData.length === 0) {
      throw new Error('获取的 PDF 数据为空');
    }

    const blob = new Blob([pdfData], { type: 'application/pdf' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = 'generated_resume.pdf';

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);

    successMessage.value = '简历生成并下载成功！';
    setTimeout(() => {
      successMessage.value = '';
    }, 3000);

  } catch (error) {
    console.error('生成并下载简历时出错:', error);
    if (error.response) {
      errorMessage.value = `服务器错误 ${error.response.status}`;
    } else if (error.request) {
      errorMessage.value = '无法连接到服务器，请检查网络';
    } else {
      errorMessage.value = `请求错误: ${error.message}`;
    }
  } finally {
    isGenerating.value = false;
  }
};

const navigateToCommunity = () => {
  router.push('/community/explore');
};

// 显示添加教育背景表单
const showAddEducationForm = () => {
  newEducation.value = {
    school: '',
    major: '',
    time: ''
  };
  editingEducationIndex.value = -1;
  showEducationForm.value = true;
};

// 编辑教育背景
const editEducation = (index) => {
  const edu = resume.value.education[index];
  newEducation.value = {
    school: edu.school,
    major: edu.major,
    time: edu.time
  };
  editingEducationIndex.value = index;
  showEducationForm.value = true;
};

// 保存教育背景
const saveEducation = () => {
  if (!newEducation.value.school || !newEducation.value.major || !newEducation.value.time) {
    alert('请填写所有教育信息');
    return;
  }

  // 确保education数组存在
  if (!resume.value.education) {
    resume.value.education = [];
  }

  if (editingEducationIndex.value > -1) {
    // 编辑现有条目
    resume.value.education[editingEducationIndex.value] = { ...newEducation.value };
  } else {
    // 添加新条目
    resume.value.education.push({ ...newEducation.value });
  }

  // 重置表单
  showEducationForm.value = false;
  editingEducationIndex.value = -1;
  newEducation.value = {
    school: '',
    major: '',
    time: ''
  };
};

// 取消编辑/添加教育背景
const cancelEducationForm = () => {
  showEducationForm.value = false;
  editingEducationIndex.value = -1;
};

// 删除教育背景
const removeEducation = (index) => {
  if (confirm('确定要删除这条教育经历吗？')) {
    resume.value.education.splice(index, 1);
  }
};
</script>

<style scoped>
/* 全局样式 */
.resume-form-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-top: 20px;
}

@media (max-width: 992px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }
}

/* 头部样式 */
.resume-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.main-title {
  font-size: 28px;
  color: #2c3e50;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
}

.resume-score-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: linear-gradient(135deg, #6366f1, #8b5cf6);
  color: white;
  padding: 8px 16px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.score-label {
  font-size: 12px;
  opacity: 0.9;
}

.score-value {
  font-size: 24px;
  font-weight: bold;
}

/* 按钮样式 */
.primary-btn {
  background: linear-gradient(to right, #3b82f6, #2563eb);
  color: white;
  border: none;
  padding: 10px 18px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.primary-btn:hover {
  background: linear-gradient(to right, #2563eb, #1d4ed8);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

.secondary-btn {
  background-color: #f3f4f6;
  color: #4b5563;
  border: 1px solid #d1d5db;
  padding: 10px 18px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.secondary-btn:hover {
  background-color: #e5e7eb;
}

.icon-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.icon-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.delete-btn {
  background-color: #f44336;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.delete-btn:hover {
  background-color: #d32f2f;
}

.icon {
  font-size: 16px;
}

/* 表单样式 */
.form-container {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05), 0 10px 15px rgba(0, 0, 0, 0.03);
  padding: 24px;
}

.section-title {
  font-size: 20px;
  color: #2c3e50;
  margin-top: 0;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.subsection-title {
  font-size: 18px;
  color: #4b5563;
  margin: 20px 0 15px;
}

.form-group {
  margin-bottom: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 10px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #4b5563;
}

input, textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s;
}

input:focus, textarea:focus {
  border-color: #3b82f6;
  outline: none;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

textarea {
  min-height: 100px;
  resize: vertical;
}

.skills-textarea {
  min-height: 120px;
}

/* 简历模块样式 */
.sections-container {
  margin-bottom: 30px;
}

.section-card {
  margin-bottom: 20px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f9fafb;
  padding: 12px 16px;
  border-bottom: 1px solid #e5e7eb;
}

.section-label {
  font-weight: 600;
  color: #4b5563;
}

.section-content {
  border: none;
  border-radius: 0;
  padding: 16px;
}

.section-actions {
  display: flex;
  padding: 8px 16px;
  background-color: #f9fafb;
  border-top: 1px solid #e5e7eb;
  gap: 10px;
}

.action-btn {
  background-color: transparent;
  color: #6b7280;
  border: 1px solid #d1d5db;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background-color: #f3f4f6;
  color: #4b5563;
}

.add-section-container {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.add-section-input {
  flex: 1;
}

.add-section-btn {
  background-color: #4b5563;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.add-section-btn:hover:not(:disabled) {
  background-color: #374151;
}

.add-section-btn:disabled {
  background-color: #d1d5db;
  cursor: not-allowed;
}

.form-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
  align-items: center;
  flex-wrap: wrap;
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
}

.modal {
  background-color: white;
  width: 90%;
  max-width: 800px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background-color: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  color: #1f2937;
}

.close-icon {
  font-size: 24px;
  color: #6b7280;
}

.modal-content {
  padding: 24px;
  max-height: 60vh;
  overflow-y: auto;
}

.modal-footer {
  padding: 16px 24px;
  background-color: #f9fafb;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
}

.resume-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.resume-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  margin-bottom: 12px;
  background-color: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.resume-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.resume-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.resume-info .title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.resume-info .score {
  font-size: 14px;
  color: #6b7280;
}

.resume-actions {
  display: flex;
  gap: 8px;
}

.share-btn {
  padding: 8px 16px;
  font-size: 14px;
  border-radius: 6px;
  background-color: #10b981;
  color: white;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
}

.share-btn:hover:not(:disabled) {
  background-color: #059669;
}

.share-btn:disabled {
  background-color: #d1d5db;
  color: #6b7280;
  cursor: not-allowed;
}

.share-btn.shared {
  background-color: #ef4444;
}

.share-btn.shared:hover {
  background-color: #dc2626;
}

/* AI功能样式 */
.ai-features {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05), 0 10px 15px rgba(0, 0, 0, 0.03);
  padding: 24px;
}

.ai-buttons {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.ai-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 20px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.ai-btn:hover {
  transform: translateY(-3px);
}

.check-btn {
  background: linear-gradient(135deg, #06b6d4, #0891b2);
  color: white;
}

.simulate-btn {
  background: linear-gradient(135deg, #8b5cf6, #7c3aed);
  color: white;
}

.ai-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.ai-result-container, .simulate-result {
  margin-top: 25px;
  padding: 20px;
  border-radius: 10px;
  background-color: #f9fafb;
  border: 1px solid #e5e7eb;
}

.ai-result-title {
  font-size: 18px;
  color: #1f2937;
  margin-top: 0;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e5e7eb;
}

.ai-section {
  margin-bottom: 20px;
}

.ai-section:last-child {
  margin-bottom: 0;
}

.ai-section .section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #4b5563;
  margin: 0 0 12px;
  padding: 0;
  border: none;
}

.ai-section .icon {
  font-size: 18px;
}

.problem-list, .suggestion-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.problem-list li, .suggestion-list li {
  position: relative;
  padding-left: 20px;
  margin-bottom: 8px;
  color: #4b5563;
  line-height: 1.5;
}

.problem-list li::before {
  content: "⚠️";
  position: absolute;
  left: 0;
  color: #f59e0b;
}

.suggestion-list li::before {
  content: "💡";
  position: absolute;
  left: 0;
  color: #3b82f6;
}

.empty-tip {
  color: #9ca3af;
  font-style: italic;
}

/* 模拟筛选结果样式 */
.score-meter {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.score-meter .score-value {
  font-size: 42px;
  font-weight: 700;
  color: #4f46e5;
  display: flex;
  align-items: baseline;
}

.score-unit {
  font-size: 18px;
  font-weight: normal;
  margin-left: 4px;
  color: #6b7280;
}

.pass-status {
  padding: 8px 16px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
}

.passed {
  background-color: #d1fae5;
  color: #065f46;
}

.failed {
  background-color: #fee2e2;
  color: #b91c1c;
}

.result-details {
  display: grid;
  gap: 20px;
}

.result-section {
  background-color: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.result-section-title {
  font-size: 16px;
  color: #4b5563;
  margin: 0 0 12px;
}

.result-text {
  margin: 0;
  color: #4b5563;
  line-height: 1.6;
}

.result-text.empty {
  color: #9ca3af;
  font-style: italic;
}

.interview-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.interview-details p {
  margin: 0;
  color: #4b5563;
}

.analysis-code {
  background-color: #f8fafc;
  padding: 12px;
  border-radius: 6px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  overflow-x: auto;
  color: #334155;
  margin: 0;
}

.error-message {
  color: #ef4444;
  font-size: 14px;
}

.success-message {
  color: #10b981;
  font-size: 14px;
}

.community-btn {
  background: linear-gradient(to right, #10b981, #059669);
  color: white;
  border: none;
  padding: 10px 18px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 10px;
}

.community-btn:hover {
  background: linear-gradient(to right, #059669, #047857);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(5, 150, 105, 0.2);
}

/* 教育背景样式 */
.education-section {
  margin-bottom: 30px;
}

.education-item {
  margin-bottom: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
  background-color: white;
}

.education-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #f9fafb;
  padding: 12px 16px;
  border-bottom: 1px solid #e5e7eb;
}

.education-school {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.education-details {
  padding: 12px 16px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.education-major, .education-time {
  font-size: 14px;
  color: #4b5563;
}

.education-actions {
  padding: 8px 16px;
  background-color: #f9fafb;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
}

.label {
  font-weight: 500;
  color: #6b7280;
}

.add-education-form {
  margin-top: 16px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background-color: #f9fafb;
}

.education-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 16px;
}

.save-btn {
  background-color: #3b82f6;
  color: white;
}

.save-btn:hover {
  background-color: #2563eb;
}

.cancel-btn {
  background-color: #f3f4f6;
  color: #4b5563;
}

.cancel-btn:hover {
  background-color: #e5e7eb;
}

.add-education-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background-color: #f3f4f6;
  color: #4b5563;
  border: 1px dashed #d1d5db;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  margin-top: 12px;
  width: 100%;
  justify-content: center;
}

.add-education-btn:hover {
  background-color: #e5e7eb;
  border-color: #9ca3af;
}

.btn-icon {
  font-size: 16px;
}
</style>