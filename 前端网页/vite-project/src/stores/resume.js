import { ref } from 'vue';
import { getResumes } from '../api/resume.js';

const resumes = ref([]);

const fetchResumes = async () => {
  try {
    const response = await getResumes();
    resumes.value = response;
  } catch (error) {
    console.error('获取简历列表时出错:', error);
  }
};

export { resumes, fetchResumes };    