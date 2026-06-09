<template>
  <div>
    <UserNav />
    <div class="page page-narrow">
      <div class="page-header">
        <h1>个人中心</h1>
        <p>你的旧书斋账户信息</p>
      </div>

      <div v-if="user" class="card detail-card">
        <div class="detail-meta" style="flex-direction:column;gap:0.75rem">
          <div><span style="color:var(--ink-faint);font-size:0.82rem">账号</span><br />{{ user.loginName }}</div>
          <div><span style="color:var(--ink-faint);font-size:0.82rem">昵称</span><br />{{ user.username }}</div>
          <div><span style="color:var(--ink-faint);font-size:0.82rem">手机</span><br />{{ user.phone || "未填写" }}</div>
          <div><span style="color:var(--ink-faint);font-size:0.82rem">邮箱</span><br />{{ user.email || "未填写" }}</div>
          <div><span style="color:var(--ink-faint);font-size:0.82rem">注册时间</span><br />{{ user.createTime || "-" }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import UserNav from "../components/UserNav.vue";
import { getMe } from "../api/user";

const user = ref(null);

const loadProfile = async () => {
  const res = await getMe();
  if (res.data.code === 200) {
    user.value = res.data.data;
  } else {
    alert(res.data.message || "获取个人信息失败");
  }
};

onMounted(loadProfile);
</script>
