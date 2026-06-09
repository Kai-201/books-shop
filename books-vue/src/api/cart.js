import request from "../utils/request";

export const addCart = (data) => request.post("/cart", data);

export const getCart = () => request.get("/cart");

export const updateCartQuantity = (id, quantity) =>
  request.put(`/cart/${id}`, { quantity });

export const deleteCartItem = (id) => request.delete(`/cart/${id}`);

export const clearCart = () => request.delete("/cart");
