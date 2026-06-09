package com.bookshop.dto;

import com.bookshop.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {

    private Integer id;
    private String orderNo;
    private Integer userId;
    private String username;
    private BigDecimal totalAmount;
    private Integer status;
    private String statusText;
    private LocalDateTime createTime;
    private List<OrderItem> items;
}
