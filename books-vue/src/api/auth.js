import request from "../utils/request";

export const loginUser = (data) => {
  return request.post("/auth/user/login", data);
};

export const loginAdmin = (data) => {
  return request.post("/auth/admin/login", data);
};

export const register = (data) => {
  return request.post("/auth/user/register", data);
};