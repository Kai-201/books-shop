<template>
  <div>
    <UserNav />
    <div class="page">
      <div class="page-header">
        <h1>我的订单</h1>
        <p>每一笔交易，都是一次与书的相遇</p>
      </div>

      <div v-if="orders.length === 0" class="empty-state">暂无订单记录</div>

      <div v-for="order in orders" :key="order.id" class="card" style="margin-bottom:1rem">
        <div style="display:flex;justify-content:space-between;align-items:flex-start;flex-wrap:wrap;gap:0.5rem">
          <div>
            <h3 style="margin-bottom:0.35rem">{{ order.orderNo }}</h3>
            <p>{{ order.createTime }}</p>
          </div>
          <span class="badge">{{ order.statusText }}</span>
        </div>
        <div class="book-price" style="margin:0.75rem 0">¥{{ order.totalAmount }}</div>
        <button class="btn btn-secondary btn-sm" @click="goDetail(order.id)">查看详情</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import UserNav from "../components/UserNav.vue";
import { getMyOrders } from "../api/order";

const router = useRouter();
const orders = ref([]);

const loadOrders = async () => {
  const res = await getMyOrders();
  if (res.data.code === 200) {
    orders.value = res.data.data;
  }
};

const goDetail = (id) => {
  router.push(`/orders/${id}`);
};

onMounted(loadOrders);
</script>
