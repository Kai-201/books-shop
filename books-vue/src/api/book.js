import request from "../utils/request";

export const getBooks = () => request.get("/books");

export const getBookById = (id) => request.get(`/books/${id}`);

export const getBooksPage = (params) => request.get("/books/page", { params });

export const getMyBooks = () => request.get("/books/my");

export const createBook = (data) => request.post("/books", data);

export const updateBook = (id, data) => request.put(`/books/${id}`, data);

export const deleteBook = (id) => request.delete(`/books/${id}`);

export const searchBooks = (keyword) => request.get("/books/search", { params: { keyword } });
