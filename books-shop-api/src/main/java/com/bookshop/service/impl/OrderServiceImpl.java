package com.bookshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.common.BusinessException;
import com.bookshop.dto.CartVO;
import com.bookshop.dto.OrderVO;
import com.bookshop.entity.Book;
import com.bookshop.entity.OrderInfo;
import com.bookshop.entity.OrderItem;
import com.bookshop.entity.User;
import com.bookshop.mapper.BookMapper;
import com.bookshop.mapper.OrderItemMapper;
import com.bookshop.mapper.OrderMapper;
import com.bookshop.mapper.UserMapper;
import com.bookshop.service.CartService;
import com.bookshop.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final CartService cartService;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                            BookMapper bookMapper, UserMapper userMapper, CartService cartService) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
        this.cartService = cartService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderInfo createFromCart(Integer userId) {
        List<CartVO> cartItems = cartService.listByUser(userId);
        if (cartItems.isEmpty()) {
            throw new BusinessException("购物车为空，无法下单");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (CartVO item : cartItems) {
            Book book = bookMapper.selectById(item.getBookId());
            if (book == null) {
                throw new BusinessException("图书不存在：" + item.getBookName());
            }
            if (book.getBooksNum() < item.getQuantity()) {
                throw new BusinessException("库存不足：" + book.getBookName());
            }
            total = total.add(item.getSubtotal());
        }

        OrderInfo order = new OrderInfo();
        order.setOrderNo(System.currentTimeMillis() + "" + (int) (Math.random() * 10000));
        order.setUserId(userId);
        order.setTotalAmount(total);
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);

        for (CartVO item : cartItems) {
            Book book = bookMapper.selectById(item.getBookId());
            book.setBooksNum(book.getBooksNum() - item.getQuantity());
            bookMapper.updateById(book);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setBookId(book.getId());
            orderItem.setBookName(book.getBookName());
            orderItem.setPrice(book.getBookPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItemMapper.insert(orderItem);
        }

        cartService.clear(userId);
        return order;
    }

    @Override
    public List<OrderVO> listByUser(Integer userId) {
        List<OrderInfo> orders = orderMapper.selectList(new LambdaQueryWrapper<OrderInfo>()
                .eq(OrderInfo::getUserId, userId)
                .orderByDesc(OrderInfo::getId));
        return orders.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public Page<OrderVO> pageForAdmin(int page, int size, String keyword) {
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(OrderInfo::getOrderNo, keyword);
        }
        wrapper.orderByDesc(OrderInfo::getId);

        Page<OrderInfo> orderPage = orderMapper.selectPage(new Page<>(page, size), wrapper);
        Page<OrderVO> voPage = new Page<>(page, size, orderPage.getTotal());
        voPage.setRecords(orderPage.getRecords().stream().map(this::toVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public OrderVO getDetail(Integer orderId, Integer userId, String role) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!"admin".equals(role) && !order.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权查看该订单");
        }
        OrderVO vo = toVO(order);
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        vo.setItems(items);
        return vo;
    }

    @Override
    public void updateStatus(Integer orderId, Integer status) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        order.setStatus(status);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer orderId) {
        OrderInfo order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        orderItemMapper.delete(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        orderMapper.deleteById(orderId);
    }

    @Override
    public Map<String, Object> statistics() {
        List<OrderInfo> all = orderMapper.selectList(null);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", all.size());
        stats.put("totalAmount", all.stream()
                .map(OrderInfo::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        stats.put("pendingPayment", all.stream().filter(o -> o.getStatus() == 0).count());
        stats.put("paid", all.stream().filter(o -> o.getStatus() == 1).count());
        stats.put("completed", all.stream().filter(o -> o.getStatus() == 2).count());
        return stats;
    }

    private OrderVO toVO(OrderInfo order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        vo.setStatusText(statusText(order.getStatus()));
        vo.setCreateTime(order.getCreateTime());

        User user = userMapper.selectById(order.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        return vo;
    }

    private String statusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        switch (status) {
            case 0: return "待付款";
            case 1: return "已付款";
            case 2: return "已完成";
            case 3: return "已取消";
            default: return "未知";
        }
    }
}
