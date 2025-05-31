<template>
  <div>
    <ResumeDetail :resume="resume" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { getResumes } from '../../api/resume.js';
import ResumeDetail from '../../components/ResumeDetail.vue';

const route = useRoute();
const resume = ref(null);

onMounted(async () => {
  try {
    const resumeId = route.params.id;
    const allResumes = await getResumes();
    resume.value = allResumes.find(r => r.id === parseInt(resumeId));
  } catch (error) {
    console.error('获取简历详情时出错:', error);
  }
});
</script>

<style scoped>
/* 简历详情页样式 */
</style>    