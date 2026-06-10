package com.bookshop.controller;

import com.bookshop.common.LoginUser;
import com.bookshop.common.Result;
import com.bookshop.dto.CartRequest;
import com.bookshop.dto.CartVO;
import com.bookshop.security.SecurityUtils;
import com.bookshop.service.CartService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@PreAuthorize("hasRole('user')")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public Result<Map<String, Object>> list() {
        LoginUser user = SecurityUtils.getCurrentUser();

        List<CartVO> items = cartService.listByUser(user.getId());
        BigDecimal total = cartService.calcTotal(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        data.put("totalAmount", total);
        return Result.ok(data);
    }

    @PostMapping
    public Result<Void> add(@Validated @RequestBody CartRequest body) {
        LoginUser user = SecurityUtils.getCurrentUser();
        cartService.add(user.getId(), body);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> updateQuantity(@PathVariable Integer id,
                                         @RequestBody Map<String, Integer> body) {
        LoginUser user = SecurityUtils.getCurrentUser();
        cartService.updateQuantity(user.getId(), id, body.get("quantity"));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        LoginUser user = SecurityUtils.getCurrentUser();
        cartService.delete(user.getId(), id);
        return Result.ok();
    }

    @DeleteMapping
    public Result<Void> clear() {
        LoginUser user = SecurityUtils.getCurrentUser();
        cartService.clear(user.getId());
        return Result.ok();
    }
}
