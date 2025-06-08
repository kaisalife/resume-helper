<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1 class="login-title">{{ isRegister ? '创建账号' : '欢迎回来' }}</h1>
        <p class="login-subtitle">{{ isRegister ? '注册一个新账号以开始使用我们的服务' : '请登录您的账号继续访问' }}</p>
      </div>
      
      <form @submit.prevent="submitForm" class="login-form">
        <div class="form-group">
          <label for="username">用户名</label>
          <div class="input-wrapper">
            <span class="input-icon">👤</span>
            <input 
              id="username"
              v-model="username" 
              placeholder="请输入您的用户名" 
              required
              autocomplete="username"
            />
          </div>
        </div>
        
        <div class="form-group">
          <label for="password">密码</label>
          <div class="input-wrapper">
            <span class="input-icon">🔒</span>
            <input 
              id="password"
              v-model="password" 
              type="password" 
              placeholder="请输入您的密码" 
              required
              autocomplete="current-password"
            />
          </div>
        </div>
        
        <div v-if="errorMessage" class="error-container">
          <div class="error-message">
            <span class="error-icon">⚠️</span>
            <span>{{ errorMessage }}</span>
          </div>
        </div>
        
        <button type="submit" class="submit-button">
          {{ isRegister ? '注册' : '登录' }}
        </button>
      </form>
      
      <div class="form-footer">
        <button @click="toggleForm" class="toggle-button">
          {{ isRegister ? '已有账号，去登录' : '没有账号，去注册' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { login, register } from '../api/auth.js';
import { login as storeLogin } from '../stores/auth.js';
import { useRouter } from 'vue-router';

const username = ref('');
const password = ref('');
const isRegister = ref(false);
const router = useRouter();
const errorMessage = ref('');

const submitForm = async () => {
  errorMessage.value = '';
  if (isRegister.value) {
    try {
      const response = await register(username.value, password.value);
      // 后端返回的是字符串，不是对象
      console.log('注册成功');
      isRegister.value = false;
    } catch (error) {
      if (error.response) {
        // 从后端获取的错误响应
        const status = error.response.status;
        const message = error.response.data;
        
        errorMessage.value = `注册失败: ${message}`;
      } else {
        // 网络错误或其他前端错误
        errorMessage.value = `注册时出错: ${error.message}`;
      }
      console.error('注册时出错:', error);
    }
  } else {
    try {
      const response = await login(username.value, password.value);
      localStorage.setItem('token', response);
      console.log('登录成功:', response);
      storeLogin();
      console.log('登录成功');
      router.push('/resume-gen');
    } catch (error) {
      // 处理登录错误
      if (error.response) {
        // 从后端获取的错误响应
        const status = error.response.status;
        const message = error.response.data;
        
        // 根据状态码显示不同的错误信息
        switch (status) {
          case 401:
            errorMessage.value = '用户名或密码错误';
            break;
          case 404:
            errorMessage.value = '用户不存在';
            break;
          case 500:
            errorMessage.value = `服务器内部错误: ${message}`;
            break;
          default:
            errorMessage.value = `登录失败: ${message}`;
        }
      } else {
        // 网络错误或其他前端错误
        errorMessage.value = `登录时出错: ${error.message}`;
      }
      console.error('登录失败:', error);
    }
  }
};

const toggleForm = () => {
  isRegister.value = !isRegister.value;
  errorMessage.value = '';
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 450px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.login-header {
  padding: 30px 30px 20px;
  text-align: center;
}

.login-title {
  margin: 0;
  font-size: 28px;
  color: #2c3e50;
  font-weight: 600;
}

.login-subtitle {
  margin: 10px 0 0;
  color: #7f8c8d;
  font-size: 16px;
}

.login-form {
  padding: 0 30px 20px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #4b5563;
}

.input-wrapper {
  position: relative;
}

.input-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #9ca3af;
  font-size: 16px;
}

input {
  width: 100%;
  padding: 12px 12px 12px 40px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.2s;
}

input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.submit-button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(to right, #3b82f6, #2563eb);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 10px;
}

.submit-button:hover {
  background: linear-gradient(to right, #2563eb, #1d4ed8);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

.form-footer {
  padding: 20px 30px;
  text-align: center;
  border-top: 1px solid #f3f4f6;
}

.toggle-button {
  background: none;
  border: none;
  color: #3b82f6;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.2s;
  padding: 0;
}

.toggle-button:hover {
  color: #1d4ed8;
  text-decoration: underline;
}

.error-container {
  margin-bottom: 15px;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: #fee2e2;
  color: #b91c1c;
  padding: 12px;
  border-radius: 8px;
  font-size: 14px;
}

.error-icon {
  font-size: 16px;
}

@media (max-width: 480px) {
  .login-card {
    border-radius: 0;
    box-shadow: none;
  }
  
  .login-header {
    padding: 20px 20px 10px;
  }
  
  .login-form {
    padding: 0 20px 15px;
  }
  
  .form-footer {
    padding: 15px 20px;
  }
}
</style>