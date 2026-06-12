package com.bookshop.service;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.bookshop.dto.BookRequest;

import com.bookshop.dto.BookVO;



import java.util.List;



public interface BookService {



    List<BookVO> listAll();



    Page<BookVO> page(int page, int size, String keyword, Integer categoryId, String uploaderType);



    List<BookVO> listByUploader(Integer uploaderId);



    BookVO getById(Integer id);



    void create(BookRequest request, Integer uploaderId);



    void update(BookRequest request, Integer operatorId, String role);



    void delete(Integer id, Integer operatorId, String role);
    /**
    * 扣减库存（乐观锁防超卖）
    * @return true=成功 false=库存不足
    */
    boolean deductStock(Integer bookId, int quantity);

}

