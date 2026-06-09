<template>
  <div>
    <UserNav />
    <div class="page">
      <div class="page-header">
        <h1>书库</h1>
        <p>在纸页与墨香之间，寻觅你的下一本好书</p>
      </div>

      <div class="toolbar">
        <input
          v-model="keyword"
          class="input"
          placeholder="搜索书名、作者…"
          @keyup.enter="loadBooks"
        />
        <select v-model="categoryId" class="select" @change="loadBooks">
          <option :value="null">全部分类</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <select v-model="uploaderType" class="select" @change="loadBooks">
          <option value="">全部来源</option>
          <option value="admin">管理员上架</option>
          <option value="user">用户发布</option>
        </select>
        <button class="btn btn-primary" @click="loadBooks">搜索</button>
      </div>

      <div v-if="books.length === 0" class="empty-state">书架空空，静待好书上架</div>

      <div v-else class="book-grid stagger">
        <div
          v-for="(book, i) in books"
          :key="book.id"
          class="card card-clickable book-card"
          :class="{ 'is-sold-out': isSoldOut(book) }"
          :style="{ animationDelay: `${i * 0.05}s` }"
          @click="goDetail(book)"
        >
          <h3>{{ book.bookName }}</h3>
          <p>{{ book.bookAuthor || "未知作者" }}</p>
          <div class="book-price">¥{{ book.bookPrice }}</div>
          <div class="book-meta">
            <span
              class="uploader-badge"
              :class="book.uploaderType === 'admin' ? 'uploader-badge--admin' : 'uploader-badge--user'"
            >
              {{ book.uploaderType === 'admin' ? '管理员' : '用户' }} · {{ book.uploaderName }}
            </span>
            <span v-if="isSoldOut(book)" class="stock-out-label">已售罄</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import UserNav from "../components/UserNav.vue";
import { getBooksPage } from "../api/book";
import { getCategories } from "../api/category";
import { useInventoryStore } from "../stores/inventory";

const router = useRouter();
const inventory = useInventoryStore();
const books = ref([]);
const categories = ref([]);
const keyword = ref("");
const categoryId = ref(null);
const uploaderType = ref("");

const isSoldOut = (book) => inventory.getStock(book.id, book.booksNum) === 0;

const loadBooks = async () => {
  const res = await getBooksPage({
    page: 1,
    size: 100,
    keyword: keyword.value || undefined,
    categoryId: categoryId.value || undefined,
    uploaderType: uploaderType.value || undefined
  });
  if (res.data.code === 200) {
    books.value = res.data.data.records || [];
    inventory.setBooks(books.value);
  }
};

const goDetail = (book) => {
  if (isSoldOut(book)) return;
  router.push(`/books/${book.id}`);
};

watch(() => inventory.revision, loadBooks);

onMounted(async () => {
  const catRes = await getCategories();
  if (catRes.data.code === 200) categories.value = catRes.data.data;
  await loadBooks();
});
</script>
