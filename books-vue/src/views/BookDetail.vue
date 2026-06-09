<template>
  <div>
    <UserNav />
    <div class="page" style="max-width:640px">
      <div v-if="book" class="card detail-card">
        <h1>{{ book.bookName }}</h1>
        <div class="detail-meta">
          <span>作者：{{ book.bookAuthor || "未知" }}</span>
          <span
            class="uploader-badge"
            :class="book.uploaderType === 'admin' ? 'uploader-badge--admin' : 'uploader-badge--user'"
          >
            上传：{{ book.uploaderType === 'admin' ? '管理员' : '用户' }} · {{ book.uploaderName }}
          </span>
        </div>
        <div class="detail-price">¥{{ book.bookPrice }}</div>
        <button class="btn btn-primary" :disabled="isSoldOut" @click="addToCart">
          {{ isSoldOut ? "已售罄" : "加入购物车" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import UserNav from "../components/UserNav.vue";
import { getBookById } from "../api/book";
import { addCart } from "../api/cart";
import { useAuthStore } from "../stores/auth";
import { useInventoryStore } from "../stores/inventory";

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();
const inventory = useInventoryStore();
const book = ref(null);

const isSoldOut = computed(() => {
  if (!book.value) return false;
  return inventory.getStock(book.value.id, book.value.booksNum) === 0;
});

const loadBook = async () => {
  const res = await getBookById(route.params.id);
  if (res.data.code === 200) {
    book.value = res.data.data;
    inventory.setBookStock(book.value.id, book.value.booksNum);
  }
};

const addToCart = async () => {
  if (!auth.isLoggedIn) {
    alert("请先登录");
    router.push("/login");
    return;
  }
  if (isSoldOut.value) return;

  const res = await addCart({ bookId: book.value.id, quantity: 1 });
  if (res.data.code === 200) {
    alert("已加入购物车");
  } else {
    alert("失败：" + (res.data.message || "操作失败"));
  }
};

watch(() => route.params.id, loadBook);
watch(() => inventory.revision, loadBook);

onMounted(loadBook);
</script>
