package com.bookshop.controller;

import com.bookshop.common.LoginUser;
import com.bookshop.common.Result;
import com.bookshop.dto.CartRequest;
import com.bookshop.dto.CartVO;
import com.bookshop.service.CartService;
import com.bookshop.util.AuthHelper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public Result<Map<String, Object>> list(HttpServletRequest request) {
        LoginUser user = AuthHelper.currentUser(request);
        AuthHelper.requireUser(user);

        List<CartVO> items = cartService.listByUser(user.getId());
        BigDecimal total = cartService.calcTotal(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        data.put("totalAmount", total);
        return Result.ok(data);
    }

    @PostMapping
    public Result<Void> add(HttpServletRequest request, @Validated @RequestBody CartRequest body) {
        LoginUser user = AuthHelper.currentUser(request);
        AuthHelper.requireUser(user);
        cartService.add(user.getId(), body);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> updateQuantity(HttpServletRequest request, @PathVariable Integer id,
                                         @RequestBody Map<String, Integer> body) {
        LoginUser user = AuthHelper.currentUser(request);
        AuthHelper.requireUser(user);
        cartService.updateQuantity(user.getId(), id, body.get("quantity"));
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest request, @PathVariable Integer id) {
        LoginUser user = AuthHelper.currentUser(request);
        AuthHelper.requireUser(user);
        cartService.delete(user.getId(), id);
        return Result.ok();
    }

    @DeleteMapping
    public Result<Void> clear(HttpServletRequest request) {
        LoginUser user = AuthHelper.currentUser(request);
        AuthHelper.requireUser(user);
        cartService.clear(user.getId());
        return Result.ok();
    }
}
