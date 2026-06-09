import { defineStore } from "pinia";
import { ref, computed } from "vue";

export const useAuthStore = defineStore("auth", () => {
  const userToken = ref(localStorage.getItem("userToken") || "");
  const adminToken = ref(localStorage.getItem("adminToken") || "");

  const isLoggedIn = computed(() => !!userToken.value);
  const isAdmin = computed(() => !!adminToken.value);

  function setUserToken(token, userInfo) {
    userToken.value = token;
    localStorage.setItem("userToken", token);
    if (userInfo) {
      localStorage.setItem("userInfo", JSON.stringify(userInfo));
    }
    adminToken.value = "";
    localStorage.removeItem("adminToken");
  }

  function setAdminToken(token) {
    adminToken.value = token;
    localStorage.setItem("adminToken", token);
    userToken.value = "";
    localStorage.removeItem("userToken");
    localStorage.removeItem("userInfo");
  }

  function logoutUser() {
    userToken.value = "";
    localStorage.removeItem("userToken");
    localStorage.removeItem("userInfo");
  }

  function logoutAdmin() {
    adminToken.value = "";
    localStorage.removeItem("adminToken");
  }

  function syncFromStorage() {
    userToken.value = localStorage.getItem("userToken") || "";
    adminToken.value = localStorage.getItem("adminToken") || "";
  }

  return {
    userToken,
    adminToken,
    isLoggedIn,
    isAdmin,
    setUserToken,
    setAdminToken,
    logoutUser,
    logoutAdmin,
    syncFromStorage
  };
});
