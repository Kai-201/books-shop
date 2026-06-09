package com.bookshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookshop.common.BusinessException;
import com.bookshop.dto.CartRequest;
import com.bookshop.dto.CartVO;
import com.bookshop.entity.Book;
import com.bookshop.entity.Cart;
import com.bookshop.mapper.BookMapper;
import com.bookshop.mapper.CartMapper;
import com.bookshop.service.CartService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final BookMapper bookMapper;

    public CartServiceImpl(CartMapper cartMapper, BookMapper bookMapper) {
        this.cartMapper = cartMapper;
        this.bookMapper = bookMapper;
    }

    @Override
    public List<CartVO> listByUser(Integer userId) {
        List<Cart> carts = cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getId));

        List<CartVO> result = new ArrayList<>();
        for (Cart cart : carts) {
            Book book = bookMapper.selectById(cart.getBookId());
            if (book == null) {
                continue;
            }
            CartVO vo = new CartVO();
            vo.setId(cart.getId());
            vo.setBookId(book.getId());
            vo.setBookSn(book.getBookSn());
            vo.setBookName(book.getBookName());
            vo.setBookAuthor(book.getBookAuthor());
            vo.setBookPrice(book.getBookPrice());
            vo.setQuantity(cart.getQuantity());
            vo.setSubtotal(book.getBookPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            result.add(vo);
        }
        return result;
    }

    @Override
    public BigDecimal calcTotal(Integer userId) {
        return listByUser(userId).stream()
                .map(CartVO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void add(Integer userId, CartRequest request) {
        Book book = bookMapper.selectById(request.getBookId());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        if (book.getBooksNum() < request.getQuantity()) {
            throw new BusinessException("库存不足");
        }

        Cart existing = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getBookId, request.getBookId()));

        if (existing != null) {
            int newQty = existing.getQuantity() + request.getQuantity();
            if (book.getBooksNum() < newQty) {
                throw new BusinessException("库存不足");
            }
            existing.setQuantity(newQty);
            cartMapper.updateById(existing);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setBookId(request.getBookId());
            cart.setQuantity(request.getQuantity());
            cartMapper.insert(cart);
        }
    }

    @Override
    public void updateQuantity(Integer userId, Integer cartId, Integer quantity) {
        Cart cart = getOwnedCart(userId, cartId);
        Book book = bookMapper.selectById(cart.getBookId());
        if (book == null) {
            throw new BusinessException("图书不存在");
        }
        if (book.getBooksNum() < quantity) {
            throw new BusinessException("库存不足");
        }
        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
    }

    @Override
    public void delete(Integer userId, Integer cartId) {
        Cart cart = getOwnedCart(userId, cartId);
        cartMapper.deleteById(cart.getId());
    }

    @Override
    public void clear(Integer userId) {
        cartMapper.delete(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
    }

    private Cart getOwnedCart(Integer userId, Integer cartId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车项不存在");
        }
        return cart;
    }
}
