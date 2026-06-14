<template>
  <div>
    <AdminNav />
    <div class="page-admin">
      <div class="page-header">
        <h1>订单管理</h1>
        <p>处理平台所有交易订单</p>
      </div>

      <div class="toolbar toolbar-admin">
        <input v-model="keyword" class="input input-admin" placeholder="搜索订单号" @keyup.enter="loadOrders" />
        <button class="btn btn-admin" @click="loadOrders">搜索</button>
      </div>

      <div class="table-wrap table-wrap-admin">
        <table class="table table-admin">
          <thead>
            <tr>
              <th>订单号</th>
              <th>用户</th>
              <th>金额</th>
              <th>状态</th>
              <th>下单时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in orders" :key="order.id">
              <td>{{ order.orderNo }}</td>
              <td>{{ order.username || order.userId }}</td>
              <td>¥{{ order.totalAmount }}</td>
              <td>
                <select
                  class="select select-admin"
                  :value="order.status"
                  @change="changeStatus(order.id, $event.target.value)"
                >
                  <option :value="0">待付款</option>
                  <option :value="1">已付款</option>
                  <option :value="2">已完成</option>
                  <option :value="3">已取消</option>
                </select>
              </td>
              <td>{{ order.createTime }}</td>
              <td>
                <button class="btn btn-ghost btn-sm" @click="goDetail(order.id)">详情</button>
                <button class="btn btn-danger btn-sm" @click="removeOrder(order.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <p v-if="orders.length === 0" class="empty-state" style="color:var(--admin-muted)">暂无订单</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import AdminNav from "../../components/AdminNav.vue";
import { getOrdersPage, updateOrderStatus, deleteOrder } from "../../api/order";

const router = useRouter();
const orders = ref([]);
const keyword = ref("");

const loadOrders = async () => {
  const res = await getOrdersPage({ page: 1, size: 100, keyword: keyword.value || undefined });
  if (res.data.code === 200) orders.value = res.data.data.records || [];
};

const changeStatus = async (id, status) => {
  const res = await updateOrderStatus(id, Number(status));
  if (res.data.code === 200) { ElMessage.success("状态已更新"); loadOrders(); }
};

const goDetail = (id) => router.push(`/orders/${id}`);

const removeOrder = async (id) => {
  if (!confirm("确定删除该订单吗？")) return;
  const res = await deleteOrder(id);
  if (res.data.code === 200) { ElMessage.success("删除成功"); loadOrders(); }
};

// WebSocket：接收新订单通知
let stompClient = null;
onMounted(() => {
  loadOrders();
  stompClient = new Client({
    webSocketFactory: () => new SockJS("http://localhost:8080/api/ws"),
    onConnect: () => {
      stompClient.subscribe("/topic/admin/orders", () => {
        ElMessage.success("有新订单！");
        loadOrders();
      });
    }
  });
  stompClient.activate();
});
onBeforeUnmount(() => {
  if (stompClient) stompClient.deactivate();
});
</script>
