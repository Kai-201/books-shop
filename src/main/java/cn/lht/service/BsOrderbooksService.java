package cn.lht.service;

import cn.lht.entity.BsOrderbooks;
import java.util.List;

/**
 * (BsOrderbooks)表服务接口
 *
 * @author makejava
 * @since 2020-04-13 16:11:54
 */
public interface BsOrderbooksService {

    /**
     * 通过ID查询单条数据
     *
     * @param bsOrderbooksid 主键
     * @return 实例对象
     */
    BsOrderbooks queryById(Integer bsOrderbooksid);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<BsOrderbooks> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param bsOrderbooks 实例对象
     * @return 实例对象
     */
    BsOrderbooks insert(BsOrderbooks bsOrderbooks);

    /**
     * 修改数据
     *
     * @param bsOrderbooks 实例对象
     * @return 实例对象
     */
    BsOrderbooks update(BsOrderbooks bsOrderbooks);

    /**
     * 通过主键删除数据
     *
     * @param bsOrderbooksid 主键
     * @return 影响行数
     */
    int deleteById(Integer bsOrderbooksid);

    /**
     * 根据订单ID查询所有商品
     * @param bsOrderId 订单ID
     * @return 商品列表
     */
    List<BsOrderbooks> selectByOrderId(Integer bsOrderId);

    /**
     * 根据订单ID删除所有商品记录
     * @param bsOrderId 订单ID
     * @return 影响行数
     */
    int deleteByOrderId(Integer bsOrderId);

}