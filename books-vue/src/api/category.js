import request from "../utils/request";

export const getCategories = () => request.get("/categories");

export const createCategory = (name) => request.post("/categories", { name });

export const deleteCategory = (id) => request.delete(`/categories/${id}`);
