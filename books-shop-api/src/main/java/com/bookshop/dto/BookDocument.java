package com.bookshop.dto;

import com.bookshop.entity.Book;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ES 索引里的图书文档。
 *
 * <p>和 BookVO 的区别：
 * BookVO 是返回给前端看的（带 uploaderName 等拼装字段），
 * BookDocument 是存到 ES 里被搜索的（只放可搜索的字段）。</p>
 */
@Data
public class BookDocument {
    private Integer id;
    private String bookSn;       // 图书编号，可搜索
    private String bookName;     // 书名，主要搜索字段
    private String bookAuthor;   // 作者，辅助搜索字段
    private Integer categoryId;
    private BigDecimal bookPrice;
    private Integer booksNum;    // 库存
    private String bookCover;
    private Integer uploaderId;
    private String uploaderName; // 上传者名字（展示用）
    private LocalDateTime createTime;

    // 无参构造（Jackson 反序列化需要）
    public BookDocument() {}

    // 全参构造（方便从 Book 对象转过来）
    public BookDocument(Book book, String uploaderName) {
        this.id = book.getId();
        this.bookSn = book.getBookSn();
        this.bookName = book.getBookName();
        this.bookAuthor = book.getBookAuthor();
        this.categoryId = book.getCategoryId();
        this.bookPrice = book.getBookPrice();
        this.booksNum = book.getBooksNum();
        this.bookCover = book.getBookCover();
        this.uploaderId = book.getUploaderId();
        this.uploaderName = uploaderName;
        this.createTime = book.getCreateTime();
    }

    // getter / setter（Jackson 序列化需要）
    // ...  你自己写或用 Lombok @Data
}