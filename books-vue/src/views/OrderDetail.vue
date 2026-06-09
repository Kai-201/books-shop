<template>
  <div>
    <UserNav v-if="!isAdmin" />
    <AdminNav v-else />

    <div :class="isAdmin ? 'page-admin' : 'page'">
      <div class="page-header">
        <h1>订单详情</h1>
        <p v-if="order">{{ order.orderNo }}</p>
      </div>

      <div v-if="order" class="card" :class="{ 'card-admin': isAdmin }">
        <div class="detail-meta">
          <span>用户：{{ order.username || order.userId }}</span>
          <span class="badge">{{ order.statusText }}</span>
        </div>
        <div class="detail-price" :style="isAdmin ? { color: 'var(--admin-accent)' } : {}">
          ¥{{ order.totalAmount }}
        </div>
        <p style="margin-bottom:1.5rem">下单时间：{{ order.createTime }}</p>

        <div class="section-title">商品明细</div>
        <div v-if="!(order.items || []).length" class="empty-state" style="padding:1.5rem">暂无明细</div>
        <div v-for="item in order.items || []" :key="item.id" class="card" :class="{ 'card-admin': isAdmin }" style="margin-bottom:0.75rem;padding:1rem">
          <strong>{{ item.bookName }}</strong>
          <p>× {{ item.quantity }} · 单价 ¥{{ item.price }}</p>
        </div>
      </div>

      <button class="btn" :class="isAdmin ? 'btn-admin' : 'btn-secondary'" style="margin-top:1rem" @click="goBack">
        返回列表
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import UserNav from "../components/UserNav.vue";
import AdminNav from "../components/AdminNav.vue";
import { getOrderById } from "../api/order";

const route = useRoute();
const router = useRouter();
const order = ref(null);
const isAdmin = computed(() => !!localStorage.getItem("adminToken"));

const loadOrder = async () => {
  const res = await getOrderById(route.params.id);
  if (res.data.code === 200) {
    order.value = res.data.data;
  } else {
    alert(res.data.message || "获取订单失败");
  }
};

const goBack = () => {
  router.push(isAdmin.value ? "/admin/orders" : "/orders");
};

onMounted(loadOrder);
</script>
