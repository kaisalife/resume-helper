<template>
  <div>
    <button @click="checkResume">语法检查</button>
    <button @click="simulatePassRate">模拟筛选</button>
    <div v-if="aiResult">{{ aiResult }}</div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { aiCheckResume, aiSimulatePassRate } from '../api/ai.js';
const resumeContent =ref(''); // 用于存储简历内容 
const aiResult = ref('');
const aiSimulateResult = ref({
  resumeId: null,           // 关联的简历 ID（类型：Long → JavaScript 中用 Number 或 null）
  passed: false,            // 是否通过简历筛选（默认未通过）
  score: 0,                 // 简历得分（默认 0 分）
  evaluation: "",           // 评价内容（空字符串）
  improvement: "",          // 改进建议（空字符串）
  interviewer: "",   // 面试官角色（默认值）
  interviewType: "",        // 预计面试类型（空字符串）
  transcript: "",           // 模拟面试对话文本（空字符串）
  analysis: ""              // 详细分析报告（空字符串）
});
const checkResume = async () => {
  try {
    const result = await aiCheckResume(resumeContent.value);
    aiResult.value = result.message;
  } catch (error) {
    console.error('AI检查简历时出错:', error);
  }
};

const simulatePassRate = async () => {
  try {
    const result = await aiSimulatePassRate(resumeContent.value);
    aiSimulateResult=result;
  } catch (error) {
    console.error('AI模拟筛选通过率时出错:', error);
  }
};

</script>

<style scoped>
button {
  margin: 5px;
}
</style>    