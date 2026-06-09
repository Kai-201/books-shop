import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth";

import Login from "../views/Login.vue";
import Register from "../views/Register.vue";
import Home from "../views/Home.vue";
import BookDetail from "../views/BookDetail.vue";
import Cart from "../views/Cart.vue";
import Order from "../views/Order.vue";
import OrderDetail from "../views/OrderDetail.vue";
import Profile from "../views/Profile.vue";
import MyBooks from "../views/MyBooks.vue";

import AdminLogin from "../views/Admin/AdminLogin.vue";
import Dashboard from "../views/Admin/Dashboard.vue";
import UserManage from "../views/Admin/UserManage.vue";
import BookManage from "../views/Admin/BookManage.vue";
import OrderManage from "../views/Admin/OrderManage.vue";

const routes = [
  { path: "/login", component: Login },
  { path: "/register", component: Register },
  { path: "/", component: Home },
  { path: "/books/:id", component: BookDetail },

  { path: "/cart", component: Cart, meta: { requiresAuth: true } },
  { path: "/orders", component: Order, meta: { requiresAuth: true } },
  { path: "/orders/:id", component: OrderDetail, meta: { requiresAuthOrAdmin: true } },
  { path: "/profile", component: Profile, meta: { requiresAuth: true } },
  { path: "/my-books", component: MyBooks, meta: { requiresAuth: true } },

  { path: "/admin/login", component: AdminLogin },
  { path: "/admin", component: Dashboard, meta: { requiresAdmin: true } },
  { path: "/admin/users", component: UserManage, meta: { requiresAdmin: true } },
  { path: "/admin/books", component: BookManage, meta: { requiresAdmin: true } },
  { path: "/admin/orders", component: OrderManage, meta: { requiresAdmin: true } }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const auth = useAuthStore();
  auth.syncFromStorage();
  const userToken = auth.userToken;
  const adminToken = auth.adminToken;

  if (to.meta.requiresAuth && !userToken) {
    return next("/login");
  }

  if (to.meta.requiresAuthOrAdmin && !userToken && !adminToken) {
    return next("/login");
  }

  if (to.meta.requiresAdmin && !adminToken) {
    return next("/admin/login");
  }

  next();
});

export default router;
