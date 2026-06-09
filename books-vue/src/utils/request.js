import axios from "axios";

const request = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 10000
});

// 请求拦截器（自动带 token）
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

export default request;