import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import './assets/styles/base.css';
import './assets/styles/utility.css';
const app = createApp(App);
app.use(router);
app.mount('#app');    
