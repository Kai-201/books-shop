package com.bookshop.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookVO {

    private Integer id;
    private String bookSn;
    private String bookName;
    private String bookAuthor;
    private Integer categoryId;
    private BigDecimal bookPrice;
    private Integer booksNum;
    private String bookCover;
    private Integer uploaderId;
    private String uploaderName;
    /** admin=管理员上架, user=用户发布 */
    private String uploaderType;
    private LocalDateTime createTime;
}
