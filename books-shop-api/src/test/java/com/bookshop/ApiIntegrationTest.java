package com.bookshop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String userToken;
    private static String adminToken;

    // ==================== 认证 ====================

    @Test
    @Order(1)
    @DisplayName("用户登录成功")
    void userLoginSuccess() throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginName\":\"user1\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.role").value("user"))
                .andReturn();

        userToken = extractToken(result);
    }

    @Test
    @Order(2)
    @DisplayName("用户登录失败 - 密码错误")
    void userLoginFail() throws Exception {
        mockMvc.perform(post("/auth/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginName\":\"user1\",\"password\":\"wrong\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @Order(3)
    @DisplayName("管理员登录成功")
    void adminLoginSuccess() throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginName\":\"admin\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.role").value("admin"))
                .andReturn();

        adminToken = extractToken(result);
    }

    @Test
    @Order(4)
    @DisplayName("用户注册成功")
    void userRegisterSuccess() throws Exception {
        mockMvc.perform(post("/auth/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginName\":\"newuser\",\"password\":\"123456\",\"username\":\"新用户\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.loginName").value("newuser"));
    }

    @Test
    @Order(5)
    @DisplayName("未登录访问购物车应返回 401")
    void cartWithoutToken() throws Exception {
        mockMvc.perform(get("/cart"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    // ==================== 图书（公开接口） ====================

    @Test
    @Order(10)
    @DisplayName("获取图书列表（无需登录）")
    void listBooks() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    @Order(11)
    @DisplayName("获取图书分类（无需登录）")
    void listCategories() throws Exception {
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(5));
    }

    @Test
    @Order(12)
    @DisplayName("获取单本图书详情")
    void getBookDetail() throws Exception {
        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.bookSn").value("BK001"));
    }

    // ==================== 购物车 ====================

    @Test
    @Order(20)
    @DisplayName("添加商品到购物车")
    void addToCart() throws Exception {
        mockMvc.perform(post("/cart")
                        .header("Authorization", bearer(userToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bookId\":1,\"quantity\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(21)
    @DisplayName("查看购物车及总价")
    void getCart() throws Exception {
        mockMvc.perform(get("/cart")
                        .header("Authorization", bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].quantity").value(2))
                .andExpect(jsonPath("$.data.totalAmount").value(178.0));
    }

    // ==================== 订单 ====================

    @Test
    @Order(30)
    @DisplayName("从购物车下单")
    void createOrder() throws Exception {
        mockMvc.perform(post("/orders")
                        .header("Authorization", bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderNo").isNotEmpty())
                .andExpect(jsonPath("$.data.totalAmount").value(178.0));
    }

    @Test
    @Order(31)
    @DisplayName("查看我的订单")
    void myOrders() throws Exception {
        mockMvc.perform(get("/orders/my")
                        .header("Authorization", bearer(userToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    // ==================== 管理员 ====================

    @Test
    @Order(40)
    @DisplayName("管理员查看用户列表")
    void adminListUsers() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(3));
    }

    @Test
    @Order(41)
    @DisplayName("管理员查看订单分页")
    void adminListOrders() throws Exception {
        mockMvc.perform(get("/orders?page=1&size=10")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records").isArray());
    }

    @Test
    @Order(42)
    @DisplayName("管理员查看订单统计")
    void adminStatistics() throws Exception {
        mockMvc.perform(get("/orders/statistics")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalOrders").value(1));
    }

    @Test
    @Order(43)
    @DisplayName("管理员新增图书分类")
    void adminAddCategory() throws Exception {
        mockMvc.perform(post("/categories")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"历史\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(44)
    @DisplayName("普通用户无权访问管理员接口")
    void userCannotAccessAdmin() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", bearer(userToken)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String extractToken(MvcResult result) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.get("data").get("token").asText();
    }
}
