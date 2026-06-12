package com.bookshop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookshop.common.LoginUser;
import com.bookshop.common.Result;
import com.bookshop.dto.OrderVO;
import com.bookshop.entity.OrderInfo;
import com.bookshop.security.SecurityUtils;
import com.bookshop.service.OrderService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单控制器。
 *
 * <p>职责：处理订单相关的 HTTP 请求，根据用户角色（普通用户 / 管理员）提供不同能力：</p>
 * <ul>
 *   <li><b>普通用户</b> —— 下单、查看自己的订单</li>
 *   <li><b>管理员</b>   —— 查看所有订单、修改状态、删除、统计</li>
 * </ul>
 *
 * <p>所有接口路径前缀为 {@code /orders}。</p>
 */
@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 用户下单 —— 将当前用户购物车中的商品转为订单。
     *
     * <p>仅普通用户可调用（{@code hasRole('user')}）。</p>
     */
    @PostMapping
    @PreAuthorize("hasRole('user')")
    public Result<OrderInfo> create() {
        LoginUser user = SecurityUtils.getCurrentUser();
        OrderInfo order = orderService.createFromCart(user.getId());
        log.info("用户下单成功: userId={}, orderNo={}, amount={}",
                user.getId(), order.getOrderNo(), order.getTotalAmount());
        return Result.ok(order);
    }
    /**
     * 查看我的订单 —— 当前用户的历史订单列表。
     *
     * <p>仅普通用户可调用。</p>
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('user')")
    public Result<List<OrderVO>> myOrders() {
        LoginUser user = SecurityUtils.getCurrentUser();
        return Result.ok(orderService.listByUser(user.getId()));
    }

    /**
     * 订单详情 —— 根据订单 ID 查看详细信息。
     *
     * <p>用户只能看自己的订单，管理员可查看任意订单（由 Service 层校验）。</p>
     */
    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Integer id) {
        LoginUser user = SecurityUtils.getCurrentUser();
        return Result.ok(orderService.getDetail(id, user.getId(), user.getRole()));
    }

    /**
     * 管理员分页查询所有订单，支持按订单号 / 用户名关键字搜索。
     *
     * <p>仅管理员可调用。</p>
     */
    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public Result<Page<OrderVO>> page(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String keyword) {
        return Result.ok(orderService.pageForAdmin(page, size, keyword));
    }

    /**
     * 修改订单状态（如：待发货 → 已发货 → 已完成）。
     *
     * <p>仅管理员可调用。请求体格式：{@code {"status": 1}}。</p>
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> updateStatus(@PathVariable Integer id,
                                     @RequestBody Map<String, Integer> body) {
        orderService.updateStatus(id, body.get("status"));
        return Result.ok();
    }

    /**
     * 取消订单 —— 仅待付款订单可取消（状态 → 已取消）。
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('user')")
    public Result<Void> cancel(@PathVariable Integer id) {
        LoginUser user = SecurityUtils.getCurrentUser();
        orderService.cancel(id, user.getId());
        log.info("用户取消订单: userId={}, orderId={}", user.getId(), id);
        return Result.ok();
    }

    /**
     * 删除订单（物理删除）。
     *
     * <p>仅管理员可调用。</p>
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public Result<Void> delete(@PathVariable Integer id) {
        orderService.delete(id);
        return Result.ok();
    }

    /**
     * 订单统计 —— 返回订单总量、总金额等汇总数据。
     *
     * <p>仅管理员可调用。</p>
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('admin')")
    public Result<Map<String, Object>> statistics() {
        return Result.ok(orderService.statistics());
    }
}
