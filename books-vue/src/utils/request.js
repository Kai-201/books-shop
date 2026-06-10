import axios from "axios";
import { ElMessage } from "element-plus";

const request = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 10000
});

// ===== 请求拦截器（自动带 token）=====
request.interceptors.request.use(config => {
  const userToken = localStorage.getItem("userToken");
  const adminToken = localStorage.getItem("adminToken");

  // 优先使用 userToken，如果没有则尝试使用 adminToken
  const token = userToken || adminToken;
  if (token) {
    config.headers.Authorization = "Bearer " + token;
  }

  return config;
});

// ===== 响应拦截器（统一错误处理）=====
request.interceptors.response.use(
  response => {
    const res = response.data;

    // 成功 — 直接放行
    if (res.code === 200) {
      return response;
    }

    // 参数校验失败（400 + 有字段错误详情）— 拼接所有字段错误
    if (res.code === 400 && res.data && typeof res.data === "object") {
      const errors = Object.values(res.data).join("；");
      ElMessage({ message: errors, type: "error", duration: 3000 });
      return response;
    }

    // 其他所有错误 — 显示后端返回的 message
    ElMessage({ message: res.message || "操作失败", type: "error", duration: 3000 });
    return response;
  },
  error => {
    // HTTP 级别错误（网络异常、超时等）
    if (error.response?.status === 401) {
      ElMessage({ message: "未登录，请先登录", type: "error", duration: 3000 });
    } else if (error.response?.status === 403) {
      ElMessage({ message: "权限不足", type: "error", duration: 3000 });
    } else if (error.response) {
      ElMessage({ message: "服务器异常，请稍后重试", type: "error", duration: 3000 });
    } else {
      ElMessage({ message: "网络异常，请检查连接", type: "error", duration: 3000 });
    }
    return Promise.reject(error);
  }
);

export default request;
