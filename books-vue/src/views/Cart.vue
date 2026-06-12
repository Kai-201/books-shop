<template>
  <div>
    <UserNav />
    <div class="page">
      <div class="page-header">
        <h1>购物车</h1>
        <p>挑选完毕，准备带走它们</p>
      </div>

      <div v-if="cartItems.length === 0" class="empty-state">购物车是空的，去书库逛逛吧</div>

      <template v-else>
        <div v-for="item in cartItems" :key="item.id" class="card cart-item">
          <div>
            <h3>{{ item.bookName }}</h3>
            <p>单价 ¥{{ item.bookPrice }}</p>
            <div class="qty-control" style="margin-top:0.75rem">
              <button @click="changeQty(item, -1)">−</button>
              <span>{{ item.quantity }}</span>
              <button :disabled="item.stock !== undefined && item.quantity >= item.stock" @click="changeQty(item, 1)">+</button>
            </div>
          </div>
          <div style="text-align:right">
            <div class="book-price">¥{{ itemSubtotal(item) }}</div>
            <button class="btn btn-danger btn-sm" style="margin-top:0.75rem" @click="removeItem(item.id)">
              移除
            </button>
          </div>
        </div>

        <div class="cart-total">合计 ¥{{ totalAmount }}</div>

        <div class="btn-group">
          <button class="btn btn-primary" @click="checkout">确认下单</button>
          <button class="btn btn-ghost" @click="clearAll">清空购物车</button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import UserNav from "../components/UserNav.vue";
import { getCart, updateCartQuantity, deleteCartItem, clearCart } from "../api/cart";
import { createOrder } from "../api/order";
import { getBookById } from "../api/book";
import { useInventoryStore } from "../stores/inventory";

const router = useRouter();
const inventory = useInventoryStore();
const cartItems = ref([]);
const totalAmount = ref(0);

const itemSubtotal = (item) => {
  const price = Number(item.bookPrice) || 0;
  return (price * item.quantity).toFixed(2);
};

const loadCart = async () => {
  try {
    const res = await getCart();
    if (res.data.code === 200) {
      const items = res.data.data.items || [];
      const enriched = await Promise.all(
        items.map(async (item) => {
          const stock = inventory.getStock(item.bookId, undefined);
          if (stock !== undefined) {
            return { ...item, stock };
          }
          const bookRes = await getBookById(item.bookId);
          const booksNum = bookRes.data.code === 200 ? bookRes.data.data.booksNum : undefined;
          if (booksNum !== undefined) {
            inventory.setBookStock(item.bookId, booksNum);
          }
          return { ...item, stock: booksNum };
        })
      );
      cartItems.value = enriched;
      totalAmount.value = res.data.data.totalAmount || 0;
    }
  } catch {
    // 错误已在响应拦截器中处理
  }
};

const changeQty = async (item, num) => {
  const newQty = item.quantity + num;
  if (newQty <= 0) return;
  if (item.stock !== undefined && newQty > item.stock) {
    ElMessage.warning("库存不足");
    return;
  }
  const res = await updateCartQuantity(item.id, newQty);
  if (res.data.code === 200) {
    item.quantity = newQty;
    totalAmount.value = cartItems.value
      .reduce((sum, i) => sum + Number(i.bookPrice) * i.quantity, 0)
      .toFixed(2);
  } else {
    loadCart();
  }
};

const removeItem = async (id) => {
  const res = await deleteCartItem(id);
  if (res.data.code === 200) loadCart();
};

const clearAll = async () => {
  if (!confirm("确定清空购物车吗？")) return;
  const res = await clearCart();
  if (res.data.code === 200) {
    cartItems.value = [];
    totalAmount.value = 0;
  }
};

const checkout = async () => {
  const res = await createOrder();
  if (res.data.code === 200) {
    inventory.bump();
    ElMessage.success("下单成功，请支付");
    const orderId = res.data.data.id;
    router.push(`/orders/${orderId}`);
  }
};

onMounted(loadCart);
</script>
