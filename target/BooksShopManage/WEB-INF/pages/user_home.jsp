    <%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>用户首页</title>
    <link rel="stylesheet" href="../static/layui/css/layui.css">
    <script src="../static/js/jquery-3.3.1.min.js"></script>
    <script src="../static/layui/layui.js"></script>
    <style>
        .header-section {
            background: #f8f8f8;
            padding: 20px 0;
            margin-bottom: 20px;
            border-bottom: 1px solid #e6e6e6;
        }
        .header-content {
            display: flex;
            justify-content: space-between;
            align-items: center;
            max-width: 900px;
            margin: 0 auto;
        }
        .header-title {
            margin: 0;
            font-size: 24px;
            color: #333;
        }
        .header-buttons {
            display: flex;
            gap: 10px;
        }
        .book-list { margin: 30px auto; max-width: 900px; }
        .book-item { 
            border-bottom: 1px solid #eee; 
            padding: 20px 0; 
            display: flex; 
            align-items: center;
            min-height: 120px;
        }
        .book-cover { 
            width: 80px; 
            height: 100px; 
            margin-right: 20px;
            flex-shrink: 0;
        }
        .book-info { 
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }
        .cart-btn { 
            margin-left: 20px; 
            align-self: center;
            flex-shrink: 0;
            width: 100px;
            height: 32px;
            line-height: 32px;
            padding: 0 15px;
        }
        .book-item .layui-btn {
            height: 32px;
            line-height: 32px;
            padding: 0 15px;
        }
    </style>
</head>
<body>
<div class="layui-container">
    <div class="header-section">
        <div class="header-content">
            <h2 class="header-title">欢迎来到二手书商城</h2>
            <div class="header-buttons">
                <a href="../bsCart/home" class="layui-btn layui-btn-primary">购物车</a>
                <a href="../bsBooks/userBooks" class="layui-btn layui-btn-normal">我上传的书籍</a>
                <a href="javascript:void(0);" onclick="logout()" class="layui-btn layui-btn-danger">退出登录</a>
            </div>
        </div>
    </div>
    <div class="book-list">
        <c:forEach items="${bsBooksList}" var="book">
            <div class="book-item">
                <img class="book-cover" src="${book.bsBookcover}" alt="封面">
                <div class="book-info">
                    <h3>${book.bsBookname}</h3>
                    <p>作者：${book.bsBookauthor}</p>
                    <p>价格：￥${book.bsBookprice}</p>
                </div>
                <button class="layui-btn cart-btn" onclick="addToCart('${book.bsBookid}')">加入购物车</button>
            </div>
        </c:forEach>
    </div>
</div>
<script>
    var userId = '${userId}';
function addToCart(bookId) {
    $.ajax({
        url: '../bsCart/add',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify({
            bsUserId: userId,
            bsGoodsId: bookId,
            bsCartNum: 1
        }),
        success: function(res) {
            layui.use('layer', function(){
                var layer = layui.layer;
                layer.msg('已加入购物车');
            });
        },
        error: function() {
            layui.use('layer', function(){
                var layer = layui.layer;
                layer.msg('加入购物车失败');
            });
        }
    });
}

// 退出登录
function logout() {
    layui.use('layer', function(){
        var layer = layui.layer;
        layer.confirm('确定要退出登录吗？', function(index) {
            $.ajax({
                url: '../bsUsers/logout',
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
</script>
</body>
</html> 