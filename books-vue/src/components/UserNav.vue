<template>
  <header class="site-header">
    <div class="site-header-inner">
      <router-link to="/" class="brand">
        <span class="brand-mark">📖</span>
        <span class="brand-name">旧书斋</span>
      </router-link>

      <nav class="site-nav">
        <router-link to="/" class="nav-link" :class="{ 'nav-link--active': isHome }">书库</router-link>
        <router-link to="/cart" class="nav-link">购物车</router-link>
        <router-link to="/orders" class="nav-link">订单</router-link>
        <router-link to="/my-books" class="nav-link">我的图书</router-link>
        <router-link to="/profile" class="nav-link">个人中心</router-link>
      </nav>

      <div class="nav-actions">
        <span class="nav-status">{{ auth.isLoggedIn ? "已登录" : "访客" }}</span>
        <template v-if="auth.isLoggedIn">
          <button class="btn btn-ghost btn-sm" @click="logout">退出</button>
        </template>
        <template v-else>
          <router-link to="/login" class="btn btn-primary btn-sm">登录</router-link>
          <router-link to="/register" class="btn btn-ghost btn-sm">注册</router-link>
        </template>
        <router-link to="/admin/login" class="btn btn-ghost btn-sm nav-admin-link">管理</router-link>
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

const isHome = computed(() => route.path === "/");

const logout = () => {
  auth.logoutUser();
  router.replace("/login");
};
</script>
