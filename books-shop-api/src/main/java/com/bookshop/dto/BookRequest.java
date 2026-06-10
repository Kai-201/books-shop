package com.bookshop.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;



@Data
public class BookRequest {

    private Integer id;

    @NotBlank(message = "图书编号不能为空")
    private String bookSn;

    @NotBlank(message = "书名不能为空")
    private String bookName;

    private String bookAuthor;

    private Integer categoryId;

    @NotNull(message = "价格不能为空")
    @Min(value = 0, message = "价格必须大于或等于0")
    private BigDecimal bookPrice;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存必须大于或等于0")
    private Integer booksNum;

    private String bookCover;
}
