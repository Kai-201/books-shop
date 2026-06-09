package com.bookshop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.common.LoginUser;
import com.bookshop.common.Result;
import com.bookshop.dto.OrderVO;
import com.bookshop.entity.OrderInfo;
import com.bookshop.service.OrderService;
import com.bookshop.util.AuthHelper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public Result<OrderInfo> create(HttpServletRequest request) {
        LoginUser user = AuthHelper.currentUser(request);
        AuthHelper.requireUser(user);
        return Result.ok(orderService.createFromCart(user.getId()));
    }

    @GetMapping("/my")
    public Result<List<OrderVO>> myOrders(HttpServletRequest request) {
        LoginUser user = AuthHelper.currentUser(request);
        AuthHelper.requireUser(user);
        return Result.ok(orderService.listByUser(user.getId()));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail(HttpServletRequest request, @PathVariable Integer id) {
        LoginUser user = AuthHelper.currentUser(request);
        return Result.ok(orderService.getDetail(id, user.getId(), user.getRole()));
    }

    @GetMapping
    public Result<Page<OrderVO>> page(HttpServletRequest request,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String keyword) {
        AuthHelper.requireAdmin(AuthHelper.currentUser(request));
        return Result.ok(orderService.pageForAdmin(page, size, keyword));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(HttpServletRequest request, @PathVariable Integer id,
                                     @RequestBody Map<String, Integer> body) {
        AuthHelper.requireAdmin(AuthHelper.currentUser(request));
        orderService.updateStatus(id, body.get("status"));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest request, @PathVariable Integer id) {
        AuthHelper.requireAdmin(AuthHelper.currentUser(request));
        orderService.delete(id);
        return Result.ok();
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(HttpServletRequest request) {
        AuthHelper.requireAdmin(AuthHelper.currentUser(request));
        return Result.ok(orderService.statistics());
    }
}
