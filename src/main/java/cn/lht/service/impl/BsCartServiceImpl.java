package cn.lht.service.impl;

import cn.lht.entity.BsCart;
import cn.lht.dao.BsCartDao;
import cn.lht.service.BsCartService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import cn.lht.dao.BsBooksDao;
import cn.lht.entity.BsBooks;

@Service("bsCartService")
public class BsCartServiceImpl implements BsCartService {
    @Resource
    private BsCartDao bsCartDao;
    @Resource
    private BsBooksDao bsBooksDao;

    @Override
    public List<BsCart> getCartByUserId(Integer bsUserId) {
        List<BsCart> cartList = bsCartDao.queryByUserId(bsUserId);
        for (BsCart cart : cartList) {
            BsBooks book = bsBooksDao.queryById(cart.getBsGoodsId());
            if (book != null) {
                cart.setBsGoodsName(book.getBsBookname());
                cart.setBsBookprice(book.getBsBookprice());
            } else {
                cart.setBsGoodsName("未知商品");
                cart.setBsBookprice(0.0);
            }
        }
        return cartList;
    }

    @Override
    public BsCart getCartByUserAndGoods(Integer bsUserId, Integer bsGoodsId) {
        return bsCartDao.queryByUserAndGoods(bsUserId, bsGoodsId);
    }

    @Override
    public int addCart(BsCart bsCart) {
        return bsCartDao.insert(bsCart);
    }

    @Override
    public int updateCart(BsCart bsCart) {
        return bsCartDao.update(bsCart);
    }

    @Override
    public int deleteCartById(Integer bsCartId) {
        return bsCartDao.deleteById(bsCartId);
    }

    @Override
    public int deleteCartByUserId(Integer bsUserId) {
        return bsCartDao.deleteByUserId(bsUserId);
    }
} 