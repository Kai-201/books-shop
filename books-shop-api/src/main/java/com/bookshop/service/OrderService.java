package com.bookshop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.dto.OrderVO;
import com.bookshop.entity.OrderInfo;

import java.util.List;
import java.util.Map;

public interface OrderService {

    OrderInfo createFromCart(Integer userId);

    List<OrderVO> listByUser(Integer userId);

    Page<OrderVO> pageForAdmin(int page, int size, String keyword);

    OrderVO getDetail(Integer orderId, Integer userId, String role);

    void updateStatus(Integer orderId, Integer status);

    void delete(Integer orderId);

    Map<String, Object> statistics();
}
