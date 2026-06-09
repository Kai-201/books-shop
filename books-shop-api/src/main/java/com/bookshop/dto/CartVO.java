package com.bookshop.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartVO {

    private Integer id;
    private Integer bookId;
    private String bookSn;
    private String bookName;
    private String bookAuthor;
    private BigDecimal bookPrice;
    private Integer quantity;
    private BigDecimal subtotal;
}
