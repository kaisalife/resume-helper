import axios from 'axios';
import config from '../config.js';

const API_BASE_URL = config.apiBaseUrl;
const testUrl = config.testUrl;

export const login = async (username, password) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/login`, { username, password });
    return response.data;
  } catch (error) {
    console.error('登录失败:', error);
    throw error;
  }
};

export const register = async (username, password) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/auth/register`, { username, password });
    return response.data;
  } catch (error) {
    console.error('注册失败:', error);
    throw error;
  }
};    