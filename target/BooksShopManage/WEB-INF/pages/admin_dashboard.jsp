<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>统一管理后台</title>
    <link rel="stylesheet" href="../static/layui/css/layui.css">
    <script src="../static/js/jquery-3.3.1.min.js"></script>
    <script src="../static/layui/layui.js"></script>
    <style>
        .admin-container { padding: 20px; }
        .tab-content { min-height: 600px; }
        .book-image { width: 60px; height: 80px; object-fit: cover; }
        .user-image { width: 50px; height: 50px; border-radius: 50%; }
    </style>
</head>
<body>
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo">统一管理后台</div>
        <ul class="layui-nav layui-layout-left">
            <li class="layui-nav-item layui-this"><a href="javascript:void(0);">管理面板</a></li>
        </ul>
        <ul class="layui-nav layui-layout-right">
            <li class="layui-nav-item">
                <a href="javascript:void(0);" onclick="logout()">退出登录</a>
            </li>
        </ul>
    </div>
    
    <div class="layui-body admin-container">
        <div class="layui-tab layui-tab-brief" lay-filter="adminTab">
            <ul class="layui-tab-title">
                <li class="layui-this" lay-id="books">书籍管理</li>
                <li lay-id="bookclass">书籍类别</li>
                <li lay-id="press">出版社管理</li>
                <li lay-id="users">用户管理</li>
                <li lay-id="orders">订单管理</li>
            </ul>
            
            <div class="layui-tab-content">
                <!-- 书籍管理 -->
                <div class="layui-tab-item layui-show" id="books-tab">
                    <div class="layui-card">
                        <div class="layui-card-header">书籍管理</div>
                        <div class="layui-card-body">
                            <table class="layui-table" id="booksTable">
                                <thead>
                                    <tr>
                                        <th>封面</th>
                                        <th>书名</th>
                                        <th>作者</th>
                                        <th>价格</th>
                                        <th>ISBN</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody id="booksTbody"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
                
                <!-- 书籍类别 -->
                <div class="layui-tab-item" id="bookclass-tab">
                    <div class="layui-card">
                        <div class="layui-card-header">书籍类别管理</div>
                        <div class="layui-card-body">
                            <table class="layui-table" id="bookclassTable">
                                <thead>
                                    <tr>
                                        <th>类别ID</th>
                                        <th>类别名称</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody id="bookclassTbody"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
                
                <!-- 出版社管理 -->
                <div class="layui-tab-item" id="press-tab">
                    <div class="layui-card">
                        <div class="layui-card-header">出版社管理</div>
                        <div class="layui-card-body">
                            <table class="layui-table" id="pressTable">
                                <thead>
                                    <tr>
                                        <th>出版社ID</th>
                                        <th>出版社编号</th>
                                        <th>出版社名称</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody id="pressTbody"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
                
                <!-- 用户管理 -->
                <div class="layui-tab-item" id="users-tab">
                    <div class="layui-card">
                        <div class="layui-card-header">用户管理</div>
                        <div class="layui-card-body">
                            <table class="layui-table" id="usersTable">
                                <thead>
                                    <tr>
                                        <th>头像</th>
                                        <th>用户名</th>
                                        <th>真实姓名</th>
                                        <th>手机号</th>
                                        <th>邮箱</th>
                                        <th>学校</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody id="usersTbody"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
                
                <!-- 订单管理 -->
                <div class="layui-tab-item" id="orders-tab">
                    <div class="layui-card">
                        <div class="layui-card-header">订单管理</div>
                        <div class="layui-card-body">
                            <table class="layui-table" id="ordersTable">
                                <thead>
                                    <tr>
                                        <th>订单号</th>
                                        <th>下单用户</th>
                                        <th>下单时间</th>
                                        <th>总金额</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody id="ordersTbody"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
var globalLayer; // 全局layer实例

layui.use(['element', 'layer'], function(){
    var element = layui.element;
    globalLayer = layui.layer; // 初始化全局layer实例
    
    // 页面加载时加载书籍管理数据
    loadBooksData();
    
    // 监听tab切换
    element.on('tab(adminTab)', function(data){
        var layId = data.elem.getAttribute('lay-id');
        switch(layId) {
            case 'books':
                loadBooksData();
                break;
            case 'bookclass':
                loadBookclassData();
                break;
            case 'press':
                loadPressData();
                break;
            case 'users':
                loadUsersData();
                break;
            case 'orders':
                loadOrdersData();
                break;
        }
    });
    
    // 加载书籍数据
    function loadBooksData() {
        $.get('../bsBooks/selectAll', function(data) {
            // 这里需要处理返回的数据
            $('#booksTbody').html('<tr><td colspan="6">数据加载中...</td></tr>');
        });
    }
    
    // 加载书籍类别数据
    function loadBookclassData() {
        $.get('../bsBookclass/selectAll', function(data) {
            var html = '';
            if(data && data.length > 0) {
                data.forEach(function(item) {
                    html += '<tr>' +
                        '<td>' + item.bsBookclassid + '</td>' +
                        '<td>' + item.bsBookclassname + '</td>' +
                        '<td><button class="layui-btn layui-btn-xs layui-btn-danger" onclick="deleteBookclass(' + item.bsBookclassid + ')">删除</button></td>' +
                        '</tr>';
                });
            } else {
                html = '<tr><td colspan="3">暂无数据</td></tr>';
            }
            $('#bookclassTbody').html(html);
        });
    }
    
    // 加载出版社数据
    function loadPressData() {
        $.get('../bsPress/selectAll', function(data) {
            var html = '';
            if(data && data.length > 0) {
                data.forEach(function(item) {
                    html += '<tr>' +
                        '<td>' + item.bsPressid + '</td>' +
                        '<td>' + item.bsPressnum + '</td>' +
                        '<td>' + item.bsPressname + '</td>' +
                        '<td><button class="layui-btn layui-btn-xs layui-btn-danger" onclick="deletePress(' + item.bsPressid + ')">删除</button></td>' +
                        '</tr>';
                });
            } else {
                html = '<tr><td colspan="4">暂无数据</td></tr>';
            }
            $('#pressTbody').html(html);
        });
    }
    
    // 加载用户数据
    function loadUsersData() {
        $.get('../bsUsers/selectAll', function(data) {
            var html = '';
            if(data && data.length > 0) {
                data.forEach(function(item) {
                    html += '<tr>' +
                        '<td><img src="' + (item.bsUserphoto || '../static/images/default-avatar.png') + '" class="user-image" alt="头像"></td>' +
                        '<td>' + item.bsLoginname + '</td>' +
                        '<td>' + item.bsTruename + '</td>' +
                        '<td>' + item.bsUserphone + '</td>' +
                        '<td>' + item.bsUseremail + '</td>' +
                        '<td>' + item.bsUniversity + '</td>' +
                        '<td><button class="layui-btn layui-btn-xs layui-btn-danger" onclick="deleteUser(' + item.bsUserid + ')">删除</button></td>' +
                        '</tr>';
                });
            } else {
                html = '<tr><td colspan="7">暂无数据</td></tr>';
            }
            $('#usersTbody').html(html);
        });
    }
    
    // 加载订单数据
    function loadOrdersData() {
        $.get('../bsOrder/adminList', function(data) {
            var html = '';
            if(data && data.length > 0) {
                data.forEach(function(item) {
                    html += '<tr>' +
                        '<td>' + item.bsOrderno + '</td>' +
                        '<td>' + item.bsLoginName + '</td>' +
                        '<td>' + item.bsCreatetime + '</td>' +
                        '<td>￥' + item.bsBooksmoney + '</td>' +
                        '<td><button class="layui-btn layui-btn-xs" onclick="viewOrderDetails(' + item.bsOrderid + ')">查看内容</button></td>' +
                        '</tr>';
                });
            } else {
                html = '<tr><td colspan="5">暂无数据</td></tr>';
            }
            $('#ordersTbody').html(html);
        });
    }
});

// 查看订单详情
function viewOrderDetails(orderId) {
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
        if (globalLayer) {
            globalLayer.open({
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
        } else {
            // 如果全局layer未初始化，则使用传统方式
            layui.use('layer', function() {
                var layer = layui.layer;
                layer.open({
                    title: '订单内容',
                    content: content,
                    area: ['600px', '400px'],
                    maxmin: true,
                    shadeClose: true,
                    zIndex: 19891015,
                    offset: '50px',
                    anim: 1,
                    moveOut: true // 允许拖出窗口
                });
            });
        }
    });
}

// 删除书籍类别
function deleteBookclass(id) {
    if (globalLayer) {
        globalLayer.confirm('确定要删除这个书籍类别吗？', function(index) {
            $.ajax({
                url: '../bsBookclass/deleteById',
                type: 'POST',
                data: JSON.stringify({bsBookclassid: id}),
                contentType: 'application/json',
                success: function(result) {
                    if(result > 0) {
                        globalLayer.msg('删除成功');
                        loadBookclassData();
                    } else {
                        globalLayer.msg('删除失败');
                    }
                }
            });
            globalLayer.close(index);
        });
    } else {
        layui.use('layer', function() {
            var layer = layui.layer;
            layer.confirm('确定要删除这个书籍类别吗？', function(index) {
                $.ajax({
                    url: '../bsBookclass/deleteById',
                    type: 'POST',
                    data: JSON.stringify({bsBookclassid: id}),
                    contentType: 'application/json',
                    success: function(result) {
                        if(result > 0) {
                            layer.msg('删除成功');
                            loadBookclassData();
                        } else {
                            layer.msg('删除失败');
                        }
                    }
                });
                layer.close(index);
            });
        });
    }
}

// 删除出版社
function deletePress(id) {
    if (globalLayer) {
        globalLayer.confirm('确定要删除这个出版社吗？', function(index) {
            $.ajax({
                url: '../bsPress/deleteById',
                type: 'POST',
                data: JSON.stringify({bsPressid: id}),
                contentType: 'application/json',
                success: function(result) {
                    if(result > 0) {
                        globalLayer.msg('删除成功');
                        loadPressData();
                    } else {
                        globalLayer.msg('删除失败');
                    }
                }
            });
            globalLayer.close(index);
        });
    } else {
        layui.use('layer', function() {
            var layer = layui.layer;
            layer.confirm('确定要删除这个出版社吗？', function(index) {
                $.ajax({
                    url: '../bsPress/deleteById',
                    type: 'POST',
                    data: JSON.stringify({bsPressid: id}),
                    contentType: 'application/json',
                    success: function(result) {
                        if(result > 0) {
                            layer.msg('删除成功');
                            loadPressData();
                        } else {
                            layer.msg('删除失败');
                        }
                    }
                });
                layer.close(index);
            });
        });
    }
}

// 删除用户
function deleteUser(id) {
    if (globalLayer) {
        globalLayer.confirm('确定要删除这个用户吗？', function(index) {
            $.ajax({
                url: '../bsUsers/deleteById',
                type: 'POST',
                data: JSON.stringify({bsUserid: id}),
                contentType: 'application/json',
                success: function(result) {
                    if(result > 0) {
                        globalLayer.msg('删除成功');
                        loadUsersData();
                    } else {
                        globalLayer.msg('删除失败');
                    }
                }
            });
            globalLayer.close(index);
        });
    } else {
        layui.use('layer', function() {
            var layer = layui.layer;
            layer.confirm('确定要删除这个用户吗？', function(index) {
                $.ajax({
                    url: '../bsUsers/deleteById',
                    type: 'POST',
                    data: JSON.stringify({bsUserid: id}),
                    contentType: 'application/json',
                    success: function(result) {
                        if(result > 0) {
                            layer.msg('删除成功');
                            loadUsersData();
                        } else {
                            layer.msg('删除失败');
                        }
                    }
                });
                layer.close(index);
            });
        });
    }
}

// 退出登录
function logout() {
    if (globalLayer) {
        globalLayer.confirm('确定要退出登录吗？', function(index) {
            $.ajax({
                url: '../bsAdmin/logout',
                type: 'POST',
                success: function(result) {
                    if(result > 0) {
                        globalLayer.msg('退出成功', {icon: 1});
                        setTimeout(function() {
                            window.location.href = '../index.jsp';
                        }, 1000);
                    } else {
                        globalLayer.msg('退出失败', {icon: 2});
                    }
                },
                error: function() {
                    globalLayer.msg('退出请求失败', {icon: 2});
                }
            });
            globalLayer.close(index);
        });
    } else {
        layui.use('layer', function() {
            var layer = layui.layer;
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
        });
    }
}
</script>
</body>
</html> 