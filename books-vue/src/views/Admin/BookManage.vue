<template>
  <div>
    <AdminNav />
    <div class="page-admin">
      <div class="page-header">
        <h1>图书管理</h1>
        <p>书库与分类维护</p>
      </div>

      <div class="card card-admin section">
        <div class="section-title" style="color:var(--admin-text)">分类管理</div>
        <div class="toolbar toolbar-admin" style="padding:0;border:none;background:transparent;margin-bottom:0.75rem">
          <input v-model="newCategory" class="input input-admin" placeholder="新分类名称" />
          <button class="btn btn-admin" @click="addCategory">添加</button>
        </div>
        <div class="tags">
          <span v-for="c in categories" :key="c.id" class="tag" style="background:var(--admin-surface-raised);border-color:var(--admin-border);color:var(--admin-text)">
            {{ c.name }}
            <button @click="removeCategory(c.id)">×</button>
          </span>
        </div>
      </div>

      <div class="toolbar toolbar-admin">
        <input v-model="keyword" class="input input-admin" placeholder="搜索书名/编号" @keyup.enter="loadBooks" />
        <select v-model="categoryId" class="select select-admin" @change="loadBooks">
          <option :value="null">全部分类</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <select v-model="uploaderType" class="select select-admin" @change="loadBooks">
          <option value="">全部来源</option>
          <option value="admin">管理员上架</option>
          <option value="user">用户发布</option>
        </select>
        <button class="btn btn-admin" @click="loadBooks">搜索</button>
        <button class="btn btn-ghost btn-sm" @click="openCreate">新增图书</button>
      </div>

      <div v-if="showForm" class="card card-admin form-card section">
        <div class="section-title" style="color:var(--admin-text)">{{ form.id ? "编辑图书" : "新增图书" }}</div>
        <input v-model="form.bookSn" class="input input-admin" placeholder="图书编号" />
        <input v-model="form.bookName" class="input input-admin" placeholder="书名" />
        <input v-model="form.bookAuthor" class="input input-admin" placeholder="作者" />
        <select v-model="form.categoryId" class="select select-admin">
          <option :value="null">选择分类</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <input v-model.number="form.bookPrice" class="input input-admin" type="number" placeholder="价格" />
        <input v-model.number="form.booksNum" class="input input-admin" type="number" placeholder="库存" />
        <input v-model="form.bookCover" class="input input-admin" placeholder="封面 URL" />
        <div class="btn-group">
          <button class="btn btn-admin" @click="submit">保存</button>
          <button class="btn btn-ghost btn-sm" @click="closeForm">取消</button>
        </div>
      </div>

      <div class="table-wrap table-wrap-admin">
        <table class="table table-admin">
          <thead>
            <tr>
              <th>编号</th>
              <th>书名</th>
              <th>作者</th>
              <th>上传者</th>
              <th>价格</th>
              <th>库存</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="book in books" :key="book.id">
              <td>{{ book.bookSn }}</td>
              <td>{{ book.bookName }}</td>
              <td>{{ book.bookAuthor || "-" }}</td>
              <td>{{ book.uploaderType === 'admin' ? '管理员' : '用户' }} · {{ book.uploaderName }}</td>
              <td>¥{{ book.bookPrice }}</td>
              <td>{{ book.booksNum }}</td>
              <td>
                <button class="btn btn-ghost btn-sm" @click="openEdit(book)">编辑</button>
                <button class="btn btn-danger btn-sm" @click="removeBook(book.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <p v-if="books.length === 0" class="empty-state" style="color:var(--admin-muted)">暂无图书</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import AdminNav from "../../components/AdminNav.vue";
import { getBooksPage, createBook, updateBook, deleteBook } from "../../api/book";
import { getCategories, createCategory, deleteCategory } from "../../api/category";
import { useInventoryStore } from "../../stores/inventory";

const inventory = useInventoryStore();

const books = ref([]);
const categories = ref([]);
const keyword = ref("");
const categoryId = ref(null);
const uploaderType = ref("");
const newCategory = ref("");
const showForm = ref(false);
const form = reactive({ id: null, bookSn: "", bookName: "", bookAuthor: "", categoryId: null, bookPrice: null, booksNum: null, bookCover: "" });

const resetForm = () => {
  Object.assign(form, { id: null, bookSn: "", bookName: "", bookAuthor: "", categoryId: null, bookPrice: null, booksNum: null, bookCover: "" });
};

const loadCategories = async () => {
  const res = await getCategories();
  if (res.data.code === 200) categories.value = res.data.data;
};

const loadBooks = async () => {
  const res = await getBooksPage({
    page: 1,
    size: 100,
    keyword: keyword.value || undefined,
    categoryId: categoryId.value || undefined,
    uploaderType: uploaderType.value || undefined
  });
  if (res.data.code === 200) books.value = res.data.data.records || [];
};

const addCategory = async () => {
  if (!newCategory.value.trim()) return;
  const res = await createCategory(newCategory.value.trim());
  if (res.data.code === 200) { newCategory.value = ""; loadCategories(); }
  else alert(res.data.message || "添加失败");
};

const removeCategory = async (id) => {
  if (!confirm("确定删除该分类吗？")) return;
  const res = await deleteCategory(id);
  if (res.data.code === 200) loadCategories();
  else alert(res.data.message || "删除失败");
};

const openCreate = () => { resetForm(); showForm.value = true; };
const openEdit = (book) => {
  Object.assign(form, { id: book.id, bookSn: book.bookSn, bookName: book.bookName, bookAuthor: book.bookAuthor || "", categoryId: book.categoryId, bookPrice: book.bookPrice, booksNum: book.booksNum, bookCover: book.bookCover || "" });
  showForm.value = true;
};
const closeForm = () => { showForm.value = false; resetForm(); };

const submit = async () => {
  const payload = { ...form };
  delete payload.id;
  const res = form.id ? await updateBook(form.id, payload) : await createBook(payload);
  if (res.data.code === 200) { alert("保存成功"); closeForm(); loadBooks(); inventory.bump(); }
  else alert(res.data.message || "保存失败");
};

const removeBook = async (id) => {
  if (!confirm("确定删除该图书吗？")) return;
  const res = await deleteBook(id);
  if (res.data.code === 200) { alert("删除成功"); loadBooks(); inventory.bump(); }
  else alert(res.data.message || "删除失败");
};

onMounted(async () => { await loadCategories(); await loadBooks(); });
</script>
