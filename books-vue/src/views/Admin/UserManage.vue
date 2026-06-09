<template>
  <div>
    <AdminNav />
    <div class="page-admin">
      <div class="page-header">
        <h1>用户管理</h1>
        <p>管理平台注册用户</p>
      </div>

      <div class="toolbar toolbar-admin">
        <input v-model="keyword" class="input input-admin" placeholder="搜索账号/昵称" @keyup.enter="loadUsers" />
        <button class="btn btn-admin" @click="loadUsers">搜索</button>
        <button class="btn btn-ghost btn-sm" @click="openCreate">新增用户</button>
      </div>

      <div v-if="showForm" class="card card-admin form-card section">
        <div class="section-title" style="color:var(--admin-text)">{{ form.id ? "编辑用户" : "新增用户" }}</div>
        <input v-model="form.loginName" class="input input-admin" placeholder="登录账号" :disabled="!!form.id" />
        <input v-model="form.password" class="input input-admin" type="password" :placeholder="form.id ? '留空则不修改' : '密码'" />
        <input v-model="form.username" class="input input-admin" placeholder="昵称" />
        <input v-model="form.phone" class="input input-admin" placeholder="手机" />
        <input v-model="form.email" class="input input-admin" placeholder="邮箱" />
        <div class="btn-group">
          <button class="btn btn-admin" @click="submit">保存</button>
          <button class="btn btn-ghost btn-sm" @click="closeForm">取消</button>
        </div>
      </div>

      <div class="table-wrap table-wrap-admin">
        <table class="table table-admin">
          <thead>
            <tr>
              <th>ID</th>
              <th>账号</th>
              <th>昵称</th>
              <th>手机</th>
              <th>邮箱</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.id }}</td>
              <td>{{ user.loginName }}</td>
              <td>{{ user.username }}</td>
              <td>{{ user.phone || "-" }}</td>
              <td>{{ user.email || "-" }}</td>
              <td>
                <button class="btn btn-ghost btn-sm" @click="openEdit(user)">编辑</button>
                <button class="btn btn-danger btn-sm" @click="removeUser(user.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <p v-if="users.length === 0" class="empty-state" style="color:var(--admin-muted)">暂无用户</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import AdminNav from "../../components/AdminNav.vue";
import { getUsersPage, createUser, updateUser, deleteUser } from "../../api/user";

const users = ref([]);
const keyword = ref("");
const showForm = ref(false);
const form = reactive({ id: null, loginName: "", password: "", username: "", phone: "", email: "" });

const resetForm = () => {
  Object.assign(form, { id: null, loginName: "", password: "", username: "", phone: "", email: "" });
};

const loadUsers = async () => {
  const res = await getUsersPage({ page: 1, size: 100, keyword: keyword.value || undefined });
  if (res.data.code === 200) users.value = res.data.data.records || [];
};

const openCreate = () => { resetForm(); showForm.value = true; };
const openEdit = (user) => {
  Object.assign(form, { id: user.id, loginName: user.loginName, password: "", username: user.username, phone: user.phone || "", email: user.email || "" });
  showForm.value = true;
};
const closeForm = () => { showForm.value = false; resetForm(); };

const submit = async () => {
  const payload = { loginName: form.loginName, username: form.username, phone: form.phone, email: form.email };
  if (form.password) payload.password = form.password;
  const res = form.id ? await updateUser(form.id, payload) : await createUser({ ...payload, password: form.password });
  if (res.data.code === 200) { alert("保存成功"); closeForm(); loadUsers(); }
  else alert(res.data.message || "保存失败");
};

const removeUser = async (id) => {
  if (!confirm("确定删除该用户吗？")) return;
  const res = await deleteUser(id);
  if (res.data.code === 200) { alert("删除成功"); loadUsers(); }
  else alert(res.data.message || "删除失败");
};

onMounted(loadUsers);
</script>
