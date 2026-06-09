<template>
  <header class="site-header admin-header">
    <div class="site-header-inner">
      <router-link to="/admin" class="brand">
        <span class="brand-mark">⚙</span>
        <span class="brand-name">旧书斋 · 管理</span>
      </router-link>

      <nav class="site-nav">
        <router-link to="/admin" class="nav-link" :class="{ 'nav-link--active': isDashboard }">控制台</router-link>
        <router-link to="/admin/users" class="nav-link">用户</router-link>
        <router-link to="/admin/books" class="nav-link">图书</router-link>
        <router-link to="/admin/orders" class="nav-link">订单</router-link>
      </nav>

      <div class="nav-actions">
        <router-link to="/" class="btn btn-ghost btn-sm">返回前台</router-link>
        <button v-if="auth.isAdmin" class="btn btn-admin btn-sm" @click="logout">退出</button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

const isDashboard = computed(() => route.path === "/admin");

const logout = () => {
  auth.logoutAdmin();
  router.push("/admin/login");
};
</script>
