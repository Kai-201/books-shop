import request from "../utils/request";

export const createOrder = () => request.post("/orders");

export const getMyOrders = () => request.get("/orders/my");

export const getOrderById = (id) => request.get(`/orders/${id}`);

export const getOrdersPage = (params) => request.get("/orders", { params });

export const updateOrderStatus = (id, status) =>
  request.put(`/orders/${id}/status`, { status });

export const deleteOrder = (id) => request.delete(`/orders/${id}`);

export const getOrderStatistics = () => request.get("/orders/statistics");

export const payOrder = (orderId) => request.post(`/payment/pay?orderId=${orderId}`);

export const cancelOrder = (orderId) => request.put(`/orders/${orderId}/cancel`);
