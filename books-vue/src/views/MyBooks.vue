<template>
  <div>
    <UserNav />
    <div class="page">
      <div class="page-header">
        <h1>我的图书</h1>
        <p>上架你的二手好书，等待下一位读者</p>
      </div>

      <div class="card form-card section">
        <div class="section-title">{{ editingId ? "编辑图书" : "发布新书上架" }}</div>
        <input v-model="form.bookSn" class="input" placeholder="图书编号" />
        <input v-model="form.bookName" class="input" placeholder="书名" />
        <input v-model="form.bookAuthor" class="input" placeholder="作者" />
        <select v-model="form.categoryId" class="select">
          <option :value="null">选择分类</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <input v-model.number="form.bookPrice" class="input" type="number" placeholder="价格" />
        <input v-model.number="form.booksNum" class="input" type="number" placeholder="库存数量（仅自己可见）" />
        <input v-model="form.bookCover" class="input" placeholder="封面 URL（可选）" />
        <div class="btn-group">
          <button class="btn btn-primary" @click="submit">{{ editingId ? "保存" : "发布" }}</button>
          <button v-if="editingId" class="btn btn-ghost" @click="resetForm">取消</button>
        </div>
      </div>

      <div v-if="books.length === 0" class="empty-state">还没有发布过图书</div>

      <div v-else class="book-grid stagger">
        <div v-for="book in books" :key="book.id" class="card book-card">
          <h3>{{ book.bookName }}</h3>
          <p>{{ book.bookSn }} · {{ book.bookAuthor || "未知" }}</p>
          <div class="book-price">¥{{ book.bookPrice }}</div>
          <div class="book-meta">
            <span class="uploader-badge uploader-badge--user">用户 · {{ book.uploaderName }}</span>
          </div>
          <div class="btn-group">
            <button class="btn btn-secondary btn-sm" @click="editBook(book)">编辑</button>
            <button class="btn btn-danger btn-sm" @click="removeBook(book.id)">删除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import UserNav from "../components/UserNav.vue";
import { getMyBooks, createBook, updateBook, deleteBook } from "../api/book";
import { getCategories } from "../api/category";
import { useInventoryStore } from "../stores/inventory";

const inventory = useInventoryStore();
const books = ref([]);
const categories = ref([]);
const editingId = ref(null);
const form = reactive({
  bookSn: "",
  bookName: "",
  bookAuthor: "",
  categoryId: null,
  bookPrice: null,
  booksNum: null,
  bookCover: ""
});

const resetForm = () => {
  editingId.value = null;
  Object.assign(form, {
    bookSn: "", bookName: "", bookAuthor: "",
    categoryId: null, bookPrice: null, booksNum: null, bookCover: ""
  });
};

const loadData = async () => {
  const [bookRes, catRes] = await Promise.all([getMyBooks(), getCategories()]);
  if (bookRes.data.code === 200) {
    books.value = bookRes.data.data;
    inventory.setBooks(books.value);
  }
  if (catRes.data.code === 200) categories.value = catRes.data.data;
};

const submit = async () => {
  const payload = { ...form };
  const res = editingId.value
    ? await updateBook(editingId.value, payload)
    : await createBook(payload);

  if (res.data.code === 200) {
    alert(editingId.value ? "保存成功" : "发布成功");
    resetForm();
    await loadData();
    inventory.bump();
  } else {
    alert(res.data.message || "操作失败");
  }
};

const editBook = (book) => {
  editingId.value = book.id;
  form.bookSn = book.bookSn;
  form.bookName = book.bookName;
  form.bookAuthor = book.bookAuthor || "";
  form.categoryId = book.categoryId;
  form.bookPrice = book.bookPrice;
  form.booksNum = book.booksNum;
  form.bookCover = book.bookCover || "";
};

const removeBook = async (id) => {
  if (!confirm("确定删除这本图书吗？")) return;
  const res = await deleteBook(id);
  if (res.data.code === 200) {
    alert("删除成功");
    await loadData();
    inventory.bump();
  } else {
    alert(res.data.message || "删除失败");
  }
};

onMounted(loadData);
</script>
