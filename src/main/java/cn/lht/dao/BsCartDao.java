package cn.lht.dao;

import cn.lht.entity.BsCart;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * (BsCart)表数据库访问层
 */
public interface BsCartDao {
    // 根据用户ID查询购物车所有商品
    List<BsCart> queryByUserId(@Param("bsUserId") Integer bsUserId);
    // 根据用户ID和商品ID查询购物车项
    BsCart queryByUserAndGoods(@Param("bsUserId") Integer bsUserId, @Param("bsGoodsId") Integer bsGoodsId);
    // 新增购物车项
    int insert(BsCart bsCart);
    // 更新购物车项
    int update(BsCart bsCart);
    // 删除购物车项
    int deleteById(@Param("bsCartId") Integer bsCartId);
    // 删除用户的全部购物车项
    int deleteByUserId(@Param("bsUserId") Integer bsUserId);
} 