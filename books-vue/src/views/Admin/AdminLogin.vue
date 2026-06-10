<template>
  <div class="auth-page">
    <div class="auth-card auth-admin">
      <h2>管理后台</h2>
      <p class="auth-subtitle">管理员身份验证</p>

      <div class="form-card">
        <input v-model="form.loginName" class="input" placeholder="管理员账号" />
        <input v-model="form.password" class="input" type="password" placeholder="密码" />
        <button class="btn btn-admin" style="width:100%;margin-top:0.5rem" @click="login">
          登录
        </button>
      </div>

      <div class="auth-links">
        <router-link to="/login" style="color:var(--admin-muted)">返回用户登录</router-link>
        <router-link to="/" style="color:var(--admin-muted)">返回首页</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import { useRouter } from "vue-router";
import { loginAdmin } from "../../api/auth";
import { useAuthStore } from "../../stores/auth";

const router = useRouter();
const auth = useAuthStore();
const form = reactive({ loginName: "", password: "" });

const login = async () => {
  try {
    const res = await loginAdmin(form);
    if (res.data.code === 200) {
      auth.setAdminToken(res.data.data.token);
      router.replace("/admin");
    }
  } catch {
    // 错误已在响应拦截器中处理
  }
};
</script>
