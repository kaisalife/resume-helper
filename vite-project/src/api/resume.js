import axios from 'axios';
import config from '../config.js';

const API_BASE_URL = config.apiBaseUrl;

// 之前的 saveResume 函数
export const saveResume = async (resumeData) => {
  try {
    // 每次请求时动态获取token
    const token = localStorage.getItem('token');
    const response = await axios.post(`${API_BASE_URL}/resume/save`, resumeData, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    return response.data;
  } catch (error) {
    console.error('保存简历失败:', error);
    throw error;
  }
};

// 之前的 getResumes 函数
export const getResumes = async () => {
  try {
    // 每次请求时动态获取token
    const token = localStorage.getItem('token');
    const response = await axios.get(`${API_BASE_URL}/resume/list`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    return response;
  } catch (error) {
    console.error('获取简历列表失败:', error);
    // 处理401错误（可选）
    if (error.response && error.response.status === 401) {
      window.location.href = '/login'; // 跳转到登录页
    }
    throw error;
  }
};

// 新增的生成简历函数
export const generateResume = async (resumeGenData) => {
  try {
    // 每次请求时动态获取token
    const token = localStorage.getItem('token');
    const response = await axios.post(`${API_BASE_URL}/resume/generate`, resumeGenData, {
      headers: {
        'Authorization': `Bearer ${token}`
      },
      responseType: 'blob' // 关键配置：指定响应类型为二进制流
    });
    return response.data;
  } catch (error) {
    console.error('生成简历失败:', error);
    throw error;
  }
};

// 修复：将第二个参数改为对象{ isShare }
export const updateResumeShareStatus = (resumeTitle, isShare) => {
  // 每次请求时动态获取token
  const token = localStorage.getItem('token');
  return axios.put(`${API_BASE_URL}/resume/share/${resumeTitle}`, { isShare }, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
};