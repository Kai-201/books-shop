package cn.lht.controller;

import cn.lht.entity.BsOrder;
import cn.lht.service.BsOrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import cn.lht.entity.BsOrderbooks;
import cn.lht.entity.BsCart;
import cn.lht.entity.BsBooks;
import cn.lht.service.BsOrderbooksService;
import cn.lht.service.BsCartService;
import cn.lht.dao.BsBooksDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

/**
 * (BsOrder)表控制层
 *
 * @author makejava
 * @since 2020-04-13 16:10:49
 */
@Controller
@RequestMapping("bsOrder")
public class BsOrderController {
    /**
     * 服务对象
     */
    @Resource
    private BsOrderService bsOrderService;

    @Autowired
    private BsOrderbooksService bsOrderbooksService;
    @Autowired
    private BsCartService bsCartService;
    @Autowired
    private BsBooksDao bsBooksDao;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    @ResponseBody
    public BsOrder selectOne(Integer id) {
        return this.bsOrderService.queryById(id);
    }

    /**
     * 用户下单接口：将购物车商品生成订单
     * @param userId 用户ID
     * @return 订单对象
     */
    @PostMapping("/create/{userId}")
    @Transactional
    @ResponseBody
    public BsOrder createOrder(@PathVariable Integer userId) {
        List<BsCart> cartList = bsCartService.getCartByUserId(userId);
        if(cartList == null || cartList.isEmpty()) return null;
        double total = 0;
        // 先校验并扣减库存
        for(BsCart cart : cartList) {
            BsBooks book = bsBooksDao.queryById(cart.getBsGoodsId());
            if(book != null) {
                // 校验库存
                if (book.getBsBooksnum() < cart.getBsCartNum()) {
                    throw new RuntimeException("库存不足，无法下单：" + book.getBsBookname());
                }
                // 扣减库存
                book.setBsBooksnum(book.getBsBooksnum() - cart.getBsCartNum());
                bsBooksDao.update(book);
                total += book.getBsBookprice() * cart.getBsCartNum();
            }
        }
        BsOrder order = new BsOrder();
        order.setBsUserid(userId);
        order.setBsOrderstatus(-2); // 未付款
        order.setBsBooksmoney(total);
        // 生成唯一订单号
        String orderNo = System.currentTimeMillis() + "" + (int)(Math.random()*10000);
        order.setBsOrderno(orderNo);
        order.setBsLivertype(0); // 默认送货上门
        order.setBsDelivermoney(0.0); // 默认0元运费
        order.setBsPaytype(0); // 默认货到付款
        order.setBsPayfrom(1); // 默认支付宝
        order.setBsIspay(0); // 默认未支付
        order.setBsUsername(""); // 收货人姓名，默认空
        order.setBsUseraddress(""); // 收货人地址，默认空
        order.setBsUserphone(""); // 收货人手机号，默认空
        order.setBsIsinvoice(0); // 默认不开发票
        order.setBsIsrefund(0); // 默认未退款
        order.setBsIsclosed(0); // 默认未完结
        order.setBsDataflag(1); // 默认有效
        // 设置创建时间为MySQL标准格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        order.setBsCreatetime(sdf.format(new Date()));
        bsOrderService.insert(order);
        for(BsCart cart : cartList) {
            BsBooks book = bsBooksDao.queryById(cart.getBsGoodsId());
            if(book != null) {
                BsOrderbooks ob = new BsOrderbooks();
                ob.setBsOrderid(order.getBsOrderid());
                ob.setBsGoodsid(book.getBsBookid());
                ob.setBsGoodsnum(cart.getBsCartNum());
                ob.setBsGoodsprice(book.getBsBookprice());
                ob.setBsGoodsname(book.getBsBookname());
                ob.setBsGoodimg(book.getBsBookcover());
                bsOrderbooksService.insert(ob);
            }
        }
        bsCartService.deleteCartByUserId(userId);
        return order;
    }

    /**
     * 管理员查询所有订单页面
     */
    @GetMapping("/adminList")
    public String adminList(Model model) {
        List<BsOrder> orderList = bsOrderService.selectAllForAdmin();
        model.addAttribute("orderList", orderList);
        return "admin_orders";
    }

    /**
     * 查询订单详情（订单内容）
     */
    @GetMapping("/details/{orderId}")
    @ResponseBody
    public List<BsOrderbooks> getOrderDetails(@PathVariable Integer orderId) {
        return bsOrderbooksService.selectByOrderId(orderId);
    }

    /**
     * 删除订单
     * @param orderId 订单ID
     * @return 删除结果
     */
    @PostMapping("/delete/{orderId}")
    @Transactional
    @ResponseBody
    public Integer deleteOrder(@PathVariable Integer orderId) {
        try {
            // 先删除订单相关的商品记录
            bsOrderbooksService.deleteByOrderId(orderId);
            // 再删除订单
            int result = bsOrderService.deleteById(orderId);
            return result > 0 ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}