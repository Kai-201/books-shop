package com.bookshop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.common.LoginUser;
import com.bookshop.common.Result;
import com.bookshop.dto.OrderVO;
import com.bookshop.entity.OrderInfo;
import com.bookshop.security.SecurityUtils;
import com.bookshop.service.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('user')")
    public Result<OrderInfo> create() {
        LoginUser user = SecurityUtils.getCurrentUser();
        return Result.ok(orderService.createFromCart(user.getId()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('user')")
    public Result<List<OrderVO>> myOrders() {
        LoginUser user = SecurityUtils.getCurrentUser();
        return Result.ok(orderService.listByUser(user.getId()));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Integer id) {
        LoginUser user = SecurityUtils.getCurrentUser();
        return Result.ok(orderService.getDetail(id, user.getId(), user.getRole()));
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public Result<Page<OrderVO>> page(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String keyword) {
        return Result.ok(orderService.pageForAdmin(page, size, keyword));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> updateStatus(@PathVariable Integer id,
                                     @RequestBody Map<String, Integer> body) {
        orderService.updateStatus(id, body.get("status"));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> delete(@PathVariable Integer id) {
        orderService.delete(id);
        return Result.ok();
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('admin')")
    public Result<Map<String, Object>> statistics() {
        return Result.ok(orderService.statistics());
    }
}
