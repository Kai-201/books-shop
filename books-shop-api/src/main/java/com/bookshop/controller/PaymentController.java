package com.bookshop.controller;

import com.bookshop.common.LoginUser;
import com.bookshop.common.Result;
import com.bookshop.security.SecurityUtils;
import com.bookshop.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 模拟支付接口
 * 生产环境会接入支付宝/微信支付的 SDK 回调
 */
@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final OrderService orderService;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 模拟支付
     * POST /api/payment/pay?orderId=1
     */
    @PostMapping("/pay")
    @PreAuthorize("hasRole('user')")
    public Result<Void> pay(@RequestParam Integer orderId) {
        LoginUser user = SecurityUtils.getCurrentUser();
        orderService.pay(orderId, user.getId());
        log.info("用户支付成功: userId={}, orderId={}", user.getId(), orderId);
        return Result.ok();
    }
}
