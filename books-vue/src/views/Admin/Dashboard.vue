<template>
  <div>
    <AdminNav />
    <div class="page-admin">
      <div class="page-header">
        <h1>控制台</h1>
        <p>旧书斋运营数据一览</p>
      </div>

      <div v-if="stats" class="stats-grid stagger">
        <div class="card card-admin stat-card">
          <h3>订单总数</h3>
          <p>{{ stats.totalOrders }}</p>
        </div>
        <div class="card card-admin stat-card">
          <h3>销售总额</h3>
          <p>¥{{ stats.totalAmount }}</p>
        </div>
        <div class="card card-admin stat-card">
          <h3>待付款</h3>
          <p>{{ stats.pendingPayment }}</p>
        </div>
        <div class="card card-admin stat-card">
          <h3>已付款</h3>
          <p>{{ stats.paid }}</p>
        </div>
        <div class="card card-admin stat-card">
          <h3>已完成</h3>
          <p>{{ stats.completed }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import AdminNav from "../../components/AdminNav.vue";
import { getOrderStatistics } from "../../api/order";

const stats = ref(null);

const loadStats = async () => {
  const res = await getOrderStatistics();
  if (res.data.code === 200) {
    stats.value = res.data.data;
  }
};

onMounted(loadStats);
</script>
