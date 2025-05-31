import axios from 'axios';
import config from '../config.js';

const API_BASE_URL = config.apiBaseUrl;
export const getSharedResumes = async () => {
   const token = localStorage.getItem('token');
  try {
    const response = await axios.get(`${API_BASE_URL}/social/share-resumes`,{
      headers: {
        'Authorization': `Bearer ${token}` // 添加 Token 到请求头
      }
    });
    return response.data;
  } catch (error) {
    console.error('获取分享的简历失败:', error);
    throw error;
  }
};    