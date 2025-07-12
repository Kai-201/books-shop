package cn.lht.service;

import cn.lht.entity.BsCart;
import java.util.List;

public interface BsCartService {
    List<BsCart> getCartByUserId(Integer bsUserId);
    BsCart getCartByUserAndGoods(Integer bsUserId, Integer bsGoodsId);
    int addCart(BsCart bsCart);
    int updateCart(BsCart bsCart);
    int deleteCartById(Integer bsCartId);
    int deleteCartByUserId(Integer bsUserId);
} 