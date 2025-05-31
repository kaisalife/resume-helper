import axios from 'axios';
import config from '../config.js';

const API_BASE_URL = config.apiBaseUrl;
export const aiCheckResume = async (resumeContent) => {
  try {
        const token = localStorage.getItem('token');
    const response = await axios.post(`${API_BASE_URL}/ai/check`, resumeContent, {
      headers: {
        'Authorization': `Bearer ${token}` // 添加 Token 到请求头
      }
    });
    return response.data;
  } catch (error) {
    console.error('AI检查简历失败:', error);
    throw error;
  }
};

export const aiSimulatePassRate = async (resumeContent) => {
  try {
        const token = localStorage.getItem('token');
    const response = await axios.post(`${API_BASE_URL}/ai/simulate`, resumeContent, {
      headers: {
        'Authorization': `Bearer ${token}` // 添加 Token 到请求头
      }
    });
    return response.data;
  } catch (error) {
    console.error('AI模拟筛选通过率失败:', error);
    throw error;
  }
};