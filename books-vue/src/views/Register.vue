<template>
  <div class="auth-page">
    <div class="auth-card">
      <h2>加入旧书斋</h2>
      <p class="auth-subtitle">注册账号，买卖二手好书</p>

      <div class="form-card">
        <input v-model="form.loginName" class="input" placeholder="登录账号" />
        <input v-model="form.password" class="input" type="password" placeholder="密码" />
        <input v-model="form.username" class="input" placeholder="昵称" />
        <button class="btn btn-primary" style="width:100%;margin-top:0.5rem" @click="submit">
          注册
        </button>
      </div>

      <div class="auth-links">
        <router-link to="/login">已有账号，去登录</router-link>
        <router-link to="/">返回首页</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import { useRouter } from "vue-router";
import { register } from "../api/auth";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const auth = useAuthStore();
const form = reactive({ loginName: "", password: "", username: "" });

const submit = async () => {
  try {
    const res = await register(form);
    if (res.data.code === 200) {
      auth.setUserToken(res.data.data.token, res.data.data);
      router.replace("/");
    }
  } catch {
    // 错误已在响应拦截器中处理
  }
};
</script>
