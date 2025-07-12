<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>我的购物车</title>
    <link rel="stylesheet" href="../static/layui/css/layui.css">
    <script src="../static/js/jquery-3.3.1.min.js"></script>
    <script src="../static/layui/layui.js"></script>
    <style>
        .cart-list { margin: 30px auto; max-width: 900px; }
        .cart-item { border-bottom: 1px solid #eee; padding: 20px 0; display: flex; align-items: center; }
        .cart-info { flex: 1; }
        .cart-num-area { min-width: 160px; text-align: right; }
        .cart-btn { margin-left: 16px; }
        .cart-actions { margin: 20px 0; }
    </style>
</head>
<body>
<div class="layui-container">
    <h2>我的购物车 <a href="../bsUsers/home" class="layui-btn layui-btn-primary">返回首页</a></h2>
    <div class="cart-actions">
        <button class="layui-btn layui-btn-danger" id="clearCart">清空购物车</button>
        <button class="layui-btn layui-btn-normal" id="checkout">去结算</button>
    </div>
    <div class="cart-list" id="cartList">
        <!-- 购物车项通过JS动态渲染 -->
    </div>
</div>
<script>
    var userId = '${userId}';
function loadCart() {
    $.get('../bsCart/list/' + userId, function(data) {
        var html = '';
        var total = 0;
        var totalPrice = 0;
        if(data && data.length > 0) {
            data.forEach(function(item) {
                html += '<div class="cart-item">'
                    + '<div class="cart-info">商品名称：' + item.bsGoodsName + '</div>'
                    + '<div class="cart-num-area">数量：'
                    + '<button class="layui-btn layui-btn-xs cart-num-btn" onclick="changeNum(' + item.bsCartId + ',' + (item.bsCartNum-1) + ')">-</button>'
                    + '<span style="margin:0 10px;">' + item.bsCartNum + '</span>'
                    + '<button class="layui-btn layui-btn-xs cart-num-btn" onclick="changeNum(' + item.bsCartId + ',' + (item.bsCartNum+1) + ')">+</button>'
                    + '</div>'
                    + '<button class="layui-btn layui-btn-danger cart-btn" onclick="deleteCart(' + item.bsCartId + ')">删除</button>'
                    + '</div>';
                total += item.bsCartNum;
                if(item.bsBookprice){ totalPrice += item.bsBookprice * item.bsCartNum; }
            });
            html += '<div style="text-align:right;font-size:18px;margin-top:20px;">总价：<span id="cartTotalPrice">' + totalPrice.toFixed(2) + '</span> 元</div>';
        } else {
            html = '<p>购物车为空</p>';
        }
        $('#cartList').html(html);
    });
}
function deleteCart(cartId) {
    $.ajax({
        url: '../bsCart/delete/' + cartId,
        type: 'DELETE',
        success: function() {
            layui.use('layer', function(){
                var layer = layui.layer;
                layer.msg('已删除');
            });
            loadCart();
        }
    });
}
function changeNum(cartId, newNum) {
    if(newNum < 1) return;
    $.ajax({
        url: '../bsCart/updateNum',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({ bsCartId: cartId, bsCartNum: newNum, bsIsCheck: 1 }),
        success: function() {
            loadCart();
        }
    });
}
$('#clearCart').click(function() {
    $.ajax({
        url: '../bsCart/clear/' + userId,
        type: 'DELETE',
        success: function() {
            layui.use('layer', function(){
                var layer = layui.layer;
                layer.msg('购物车已清空');
            });
            loadCart();
        }
    });
});
$('#checkout').click(function() {
    $.ajax({
        url: '../bsOrder/create/' + userId,
        type: 'POST',
        success: function(data) {
            layui.use('layer', function(){
                var layer = layui.layer;
                if(data && data.bsOrderid) {
                    layer.msg('下单成功，订单号：' + data.bsOrderid);
                    loadCart();
                } else {
                    layer.msg('下单失败，购物车为空');
                }
            });
        },
        error: function() {
            layui.use('layer', function(){
                var layer = layui.layer;
                layer.msg('下单失败');
            });
        }
    });
});
$(function(){
    loadCart();
});
</script>
</body>
</html> 