import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import ResumeGen from '../views/ResumeGen.vue';
import Explore from '../views/Community/Explore.vue';
import Detail from '../views/Community/Detail.vue';
import { isLoggedIn } from '../stores/auth.js';

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/resume-gen',
    name: 'ResumeGen',
    component: ResumeGen,
    meta: { requiresAuth: true }
  },
  {
    path: '/community/explore',
    name: 'Explore',
    component: Explore,
    meta: { requiresAuth: true }
  },
  {
    path: '/community/detail/:id',
    name: 'Detail',
    component: Detail,
    meta: { requiresAuth: true }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth &&!isLoggedIn.value) {
    next('/');
  } else {
    next();
  }
});

export default router;    