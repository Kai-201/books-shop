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

      <div v-if="order && order.status === 0" style="margin-top:1rem">
        <button class="btn btn-primary" style="margin-right:0.5rem" @click="pay">
          去支付
        </button>
        <button class="btn btn-danger" style="margin-right:0.5rem" @click="cancel">
          取消订单
        </button>
        <button class="btn" :class="isAdmin ? 'btn-admin' : 'btn-secondary'" @click="goBack">
          返回列表
        </button>
      </div>
      <button v-else class="btn" :class="isAdmin ? 'btn-admin' : 'btn-secondary'" style="margin-top:1rem" @click="goBack">
        返回列表
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import UserNav from "../components/UserNav.vue";
import AdminNav from "../components/AdminNav.vue";
import { getOrderById, payOrder, cancelOrder } from "../api/order";

const route = useRoute();
const router = useRouter();
const order = ref(null);
const isAdmin = computed(() => !!localStorage.getItem("adminToken"));

const loadOrder = async () => {
  const res = await getOrderById(route.params.id);
  if (res.data.code === 200) {
    order.value = res.data.data;
  }
};

const pay = async () => {
  const res = await payOrder(route.params.id);
  if (res.data.code === 200) {
    ElMessage.success("支付成功");
    loadOrder();
  }
};

const cancel = async () => {
  if (!confirm("确定取消该订单吗？")) return;
  const res = await cancelOrder(route.params.id);
  if (res.data.code === 200) {
    ElMessage.success("订单已取消");
    loadOrder();
  }
};

// 从 JWT 解析 userId
const getUserId = () => {
  const token = localStorage.getItem("userToken");
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.id;
  } catch { return null; }
};

// WebSocket：接收订单状态推送
let stompClient = null;
const connectWebSocket = () => {
  const userId = getUserId();
  if (!userId) return;
  stompClient = new Client({
    webSocketFactory: () => new SockJS("http://localhost:8080/api/ws"),
    onConnect: () => {
      stompClient.subscribe(`/topic/orders/${userId}`, (message) => {
        ElMessage.success(`订单状态更新：${message.body}`);
        loadOrder();
      });
    }
  });
  stompClient.activate();
};

const goBack = () => {
  router.push(isAdmin.value ? "/admin/orders" : "/orders");
};

onMounted(() => {
  loadOrder();
  if (!isAdmin.value) connectWebSocket();
});
onBeforeUnmount(() => {
  if (stompClient) stompClient.deactivate();
});
</script>
