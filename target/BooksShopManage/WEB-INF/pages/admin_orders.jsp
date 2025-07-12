<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>订单管理</title>
    <link rel="stylesheet" href="../static/layui/css/layui.css">
    <script src="../static/js/jquery-3.3.1.min.js"></script>
    <script src="../static/layui/layui.js"></script>
</head>
<body>
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo">后台管理</div>
        <ul class="layui-nav layui-layout-left">
            <li class="layui-nav-item"><a href="../bsBooks/selectAll">书籍管理</a></li>
            <li class="layui-nav-item" style="display:none;"><a href="../bsBookclass/selectAll">书籍类别管理</a></li>
            <li class="layui-nav-item" style="display:none;"><a href="../bsPress/selectAll">出版社管理</a></li>
            <li class="layui-nav-item"><a href="../bsUsers/selectAll">用户管理</a></li>
            <li class="layui-nav-item layui-this"><a href="../bsOrder/adminList">订单管理</a></li>
        </ul>
        <ul class="layui-nav layui-layout-right">
            <li class="layui-nav-item">
                <a href="javascript:void(0);" onclick="logout()">退出登录</a>
            </li>
        </ul>
    </div>
    <div class="layui-body" style="padding: 15px;">
        <div class="layui-tab layui-tab-card">
            <ul class="layui-tab-title">
                <li class="layui-this">订单列表</li>
            </ul>
            <div class="layui-tab-content">
                <table class="layui-table">
                    <colgroup>
                        <col width="150">
                        <col width="150">
                        <col width="200">
                        <col>
                        <col width="150">
                    </colgroup>
                    <thead>
                    <tr>
                        <th>订单号</th>
                        <th>下单用户</th>
                        <th>下单时间</th>
                        <th>总金额</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${orderList}" var="order">
                        <tr>
                            <td>${order.bsOrderno}</td>
                            <td>${order.bsLoginName}</td>
                            <td>${order.bsCreatetime}</td>
                            <td>￥${order.bsBooksmoney}</td>
                            <td>
                                <div style="display: flex; gap: 8px; align-items: center; justify-content: center;">
                                    <button class="layui-btn layui-btn-xs" onclick="viewDetails(${order.bsOrderid})">查看内容</button>
                                    <button class="layui-btn layui-btn-danger layui-btn-xs" onclick="deleteOrder(${order.bsOrderid})">删除订单</button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script>
    var layer; // 全局变量存储layer实例
    
    // 页面加载时初始化layer
    layui.use('layer', function(){
        layer = layui.layer;
    });
    
    function viewDetails(orderId) {
        $.get('../bsOrder/details/' + orderId, function (data) {
            var content = '<table class="layui-table"><thead><tr><th>商品名称</th><th>单价</th><th>数量</th></tr></thead><tbody>';
            if(data && data.length > 0) {
                data.forEach(function (item) {
                    content += '<tr><td>' + item.bsGoodsname + '</td><td>￥' + item.bsGoodsprice + '</td><td>' + item.bsGoodsnum + '</td></tr>';
                });
            } else {
                content += '<tr><td colspan="3">暂无商品</td></tr>';
            }
            content += '</tbody></table>';

            // 使用全局layer实例
            layer.open({
                title: '订单内容',
                content: content,
                area: ['600px', '400px'],
                maxmin: true, // 允许最大化最小化
                shadeClose: true, // 点击遮罩关闭
                zIndex: 19891015, // 设置较高的层级
                offset: '50px', // 距离顶部50px
                anim: 1, // 动画效果
                moveOut: true // 允许拖出窗口
            });
        });
    }

    function deleteOrder(orderId) {
        // 确认删除
        layer.confirm('确定要删除这个订单吗？删除后无法恢复！', {
            icon: 3,
            title: '确认删除'
        }, function(index) {
            // 发送删除请求
            $.ajax({
                url: '../bsOrder/delete/' + orderId,
                type: 'POST',
                success: function(data) {
                    if(data > 0) {
                        layer.msg('订单删除成功', {icon: 1});
                        // 刷新页面
                        setTimeout(function() {
                            location.reload();
                        }, 1000);
                    } else {
                        layer.msg('订单删除失败', {icon: 2});
                    }
                },
                error: function() {
                    layer.msg('删除请求失败', {icon: 2});
                }
            });
            layer.close(index);
        });
    }

    // 退出登录
    function logout() {
        layer.confirm('确定要退出登录吗？', function(index) {
            $.ajax({
                url: '../bsAdmin/logout',
                type: 'POST',
                success: function(result) {
                    if(result > 0) {
                        layer.msg('退出成功', {icon: 1});
                        setTimeout(function() {
                            window.location.href = '../index.jsp';
                        }, 1000);
                    } else {
                        layer.msg('退出失败', {icon: 2});
                    }
                },
                error: function() {
                    layer.msg('退出请求失败', {icon: 2});
                }
            });
            layer.close(index);
        });
    }
</script>

</body>
</html> 