package com.bookshop.service;

import com.bookshop.dto.CartRequest;
import com.bookshop.dto.CartVO;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    List<CartVO> listByUser(Integer userId);

    BigDecimal calcTotal(Integer userId);

    void add(Integer userId, CartRequest request);

    void updateQuantity(Integer userId, Integer cartId, Integer quantity);

    void delete(Integer userId, Integer cartId);

    void clear(Integer userId);
}
