package com.bookshop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("book")
public class Book {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String bookSn;
    private String bookName;
    private String bookAuthor;
    private Integer categoryId;
    private BigDecimal bookPrice;
    private Integer booksNum;
    private String bookCover;
    private Integer uploaderId;
    private LocalDateTime createTime;
}
