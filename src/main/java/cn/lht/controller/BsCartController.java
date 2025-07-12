package cn.lht.controller;

import cn.lht.entity.BsCart;
import cn.lht.service.BsCartService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import javax.servlet.http.HttpSession;
import cn.lht.entity.BsUsers;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("bsCart")
public class BsCartController {
    @Resource
    private BsCartService bsCartService;

    // 查询用户购物车
    @GetMapping("/list/{userId}")
    @ResponseBody
    public List<BsCart> getCartList(@PathVariable Integer userId) {
        return bsCartService.getCartByUserId(userId);
    }

    // 添加或更新购物车项
    @PostMapping("/add")
    @ResponseBody
    public int addCart(@RequestBody BsCart bsCart) {
        // 先查是否已存在该商品
        BsCart exist = bsCartService.getCartByUserAndGoods(bsCart.getBsUserId(), bsCart.getBsGoodsId());
        if (exist != null) {
            exist.setBsCartNum(exist.getBsCartNum() + bsCart.getBsCartNum());
            return bsCartService.updateCart(exist);
        } else {
            bsCart.setBsIsCheck(1);
            return bsCartService.addCart(bsCart);
        }
    }

    // 删除购物车项
    @DeleteMapping("/delete/{cartId}")
    @ResponseBody
    public int deleteCart(@PathVariable Integer cartId) {
        return bsCartService.deleteCartById(cartId);
    }

    // 清空用户购物车
    @DeleteMapping("/clear/{userId}")
    @ResponseBody
    public int clearCart(@PathVariable Integer userId) {
        return bsCartService.deleteCartByUserId(userId);
    }

    // 更新购物车项数量
    @PostMapping("/updateNum")
    @ResponseBody
    public int updateCartNum(@RequestBody BsCart bsCart) {
        return bsCartService.updateCart(bsCart);
    }

    /**
     * 购物车页面跳转
     */
    @RequestMapping("home")
    public String cartHome(HttpSession session, Model model) {
        BsUsers user = (BsUsers) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("userId", user.getBsUserid());
        }
        return "user_cart";
    }
} 