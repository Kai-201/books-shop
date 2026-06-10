<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>欢迎回来</h2>
      <p class="auth-subtitle">登录旧书斋，继续你的阅读之旅</p>

      <div class="form-card">
        <input v-model="form.loginName" class="input" placeholder="用户名" />
        <input v-model="form.password" class="input" type="password" placeholder="密码" />
        <button class="btn btn-primary" style="width:100%;margin-top:0.5rem" @click="login">
          登录
        </button>
      </div>

      <div class="auth-links">
        <router-link to="/register">没有账号？注册</router-link>
        <router-link to="/admin/login">管理员入口</router-link>
        <router-link to="/">返回首页</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { loginUser } from "../api/auth";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const auth = useAuthStore();
const form = reactive({ loginName: "", password: "" });

const login = async () => {
  try {
    const res = await loginUser(form);
    if (res.data.code === 200) {
      auth.setUserToken(res.data.data.token, res.data.data);
      ElMessage.success("登录成功");
      router.replace("/");
    }
  } catch {
    // 错误已在响应拦截器中处理
  }
};
</script>
