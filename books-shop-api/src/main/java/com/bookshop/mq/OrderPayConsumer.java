package com.bookshop.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookshop.config.RabbitMQConfig;
import com.bookshop.entity.OrderItem;
import com.bookshop.mapper.OrderItemMapper;
import com.bookshop.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component  // Spring 管理，启动时自动开始监听
public class OrderPayConsumer {

    private final OrderItemMapper orderItemMapper;
    private final BookService bookService;

    public OrderPayConsumer(OrderItemMapper orderItemMapper, BookService bookService) {
        this.orderItemMapper = orderItemMapper;
        this.bookService = bookService;
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_PAY_QUEUE)
    //       ↑ 告诉 Spring："这个队列有消息就叫我"
    public void handlePay(Integer orderId) {
        // 参数类型和发消息时一致（Integer），Spring 自动反序列化
        log.info("收到支付消息: orderId={}", orderId);

        // 查出订单明细（每本书的 ID 和购买数量）
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));

        // 逐条扣库存（乐观锁在 deductStock 内部：select → updateById → 版本号冲突则重试）
        for (OrderItem item : items) {
            bookService.deductStock(item.getBookId(), item.getQuantity());
        }
        log.info("支付处理完成: orderId={}", orderId);
    }
}
