<template>
  <div>
    <h1>简历生成</h1>
    <ResumeForm />
    <button @click="goToCommunity">前往简历社区</button>
  </div>
</template>

<script setup>
import ResumeForm from '../components/ResumeForm.vue';
import { useRouter } from 'vue-router';
const router = useRouter();
const goToCommunity = () => {
  router.push('/community/explore');
};
</script>

<style scoped>
button {
  margin-top: 20px;
}
</style>    