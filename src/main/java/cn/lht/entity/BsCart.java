package cn.lht.entity;

import java.io.Serializable;

/**
 * (BsCart)实体类，对应bs_carts表
 */
public class BsCart implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 自增ID */
    private Integer bsCartId;
    /** 用户ID */
    private Integer bsUserId;
    /** 是否选中 */
    private Integer bsIsCheck;
    /** 商品ID（图书ID） */
    private Integer bsGoodsId;
    /** 商品数量 */
    private Integer bsCartNum;

    public Integer getBsCartId() { return bsCartId; }
    public void setBsCartId(Integer bsCartId) { this.bsCartId = bsCartId; }
    public Integer getBsUserId() { return bsUserId; }
    public void setBsUserId(Integer bsUserId) { this.bsUserId = bsUserId; }
    public Integer getBsIsCheck() { return bsIsCheck; }
    public void setBsIsCheck(Integer bsIsCheck) { this.bsIsCheck = bsIsCheck; }
    public Integer getBsGoodsId() { return bsGoodsId; }
    public void setBsGoodsId(Integer bsGoodsId) { this.bsGoodsId = bsGoodsId; }
    public Integer getBsCartNum() { return bsCartNum; }
    public void setBsCartNum(Integer bsCartNum) { this.bsCartNum = bsCartNum; }
} 