import { ref } from 'vue';

const isLoggedIn = ref(false);

const login = () => {
  isLoggedIn.value = true;
};

const logout = () => {
  isLoggedIn.value = false;
};

export { isLoggedIn, login, logout };    