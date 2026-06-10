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

    // 参数校验失败（400）- 把字段错误拼成完整提示
    if (res.code === 400 && res.data && typeof res.data === "object") {
      const errors = Object.values(res.data).join("；");
      ElMessage.error(errors);
      return response;
    }

    // 未登录（401）
    if (res.code === 401) {
      ElMessage.error(res.message || "请先登录");
      return response;
    }

    // 权限不足（403）
    if (res.code === 403) {
      ElMessage.error(res.message || "权限不足");
      return response;
    }

    return response;
  },
  error => {
    // HTTP 级别错误
    if (error.response?.status === 401) {
      ElMessage.error("未登录，请先登录");
    } else if (error.response?.status === 403) {
      ElMessage.error("权限不足");
    } else if (error.response) {
      ElMessage.error("服务器异常，请稍后重试");
    } else {
      ElMessage.error("网络异常，请检查连接");
    }
    return Promise.reject(error);
  }
);

export default request;
