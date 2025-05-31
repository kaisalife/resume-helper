<template>
  <nav>
    <ul>
      <li v-if="!isLoggedIn"><router-link to="/">登录</router-link></li>
      <li v-if="isLoggedIn"><router-link to="/resume-gen">简历生成</router-link></li>
      <li v-if="isLoggedIn"><router-link to="/community/explore">简历社区</router-link></li>
      <li v-if="isLoggedIn"><button @click="logout">退出登录</button></li>
    </ul>
  </nav>
</template>

<script setup>
import { computed } from 'vue';
import { isLoggedIn, logout as storeLogout } from '../stores/auth.js';
import { useRouter } from 'vue-router';

const router = useRouter();
const logout = () => {
  storeLogout();
  router.push('/');
};

const computedIsLoggedIn = computed(() => isLoggedIn.value);
</script>

<style scoped>
nav ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
  display: flex;
  background-color: #f4f4f4;
}

nav ul li {
  padding: 10px;
}

nav ul li a {
  text-decoration: none;
  color: #333;
}

button {
  padding: 0;
  background: none;
  border: none;
  cursor: pointer;
  color: #333;
  text-decoration: none;
}

button:hover {
  text-decoration: underline;
}
</style>    