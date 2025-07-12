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
        .cart-btn { margin-left: 20px; }
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
        if(data && data.length > 0) {
            data.forEach(function(item) {
                html += '<div class="cart-item">'
                    + '<div class="cart-info">商品ID: ' + item.bsGoodsId + ' 数量: ' + item.bsCartNum + '</div>'
                    + '<button class="layui-btn layui-btn-danger cart-btn" onclick="deleteCart(' + item.bsCartId + ')">删除</button>'
                    + '</div>';
                total += item.bsCartNum;
            });
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