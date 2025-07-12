<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>后台管理</title>
   <link rel="stylesheet" href="../static/layui/css/layui.css">
    <script src="../static/js/jquery-3.3.1.min.js"></script>
    <script src="../static/layui/layui.js"></script>
    <script>
        $(function () {
            layui.use('element', function(){
                var element = layui.element;
            });
             var tbody = $("#tbody");
            var loginName = [];
            var a = 0;
            <c:forEach var="b" items="${bsPublishbooksList}">
                loginName[a] = "${b.bsLoginname}";
                var url = "${b.bsBookcover}";
                var bookname = "${b.bsBookname}";
                var press = "${b.bsPressname}";
                var price = "${b.bsBookprice}";
                var num = "${b.bsBooknum}";
                var bsProvince = "${b.bsProvince}";
                 a++;
                if(num > 0){
                    var $tr = $("<tr>\n" +
                    "                <td>\n" +
                    "                    <span class = \"image\"><img src="+url+" alt=\"\"></span>\n" +
                    "                    <span>\n" +
                    "                        <h6 class = \"bookName\">"+bookname+"</h6>\n" +
                    "                        <span class=\"press\">"+press+"</span>\n" +
                    "                    </span>\n" +
                    "                </td>\n" +
                    "                <td>￥ "+price+"</td>\n" +
                    "                <td>"+url+"</td>\n" +
                    "                <td>"+num+"</td>\n" +
                    "                <td>"+bsProvince+"</td>\n" +
                    "                <td><a class=\"layui-btn layui-btn-danger layui-btn-xs\">获取借书人联系方式</a>" +
                    "            </tr>");
                tbody.append($tr);
                }
            </c:forEach>
            $("#tbody").on('click','.layui-btn.layui-btn-danger.layui-btn-xs',function () {
                let $tr = $(this).parent().parent();
               let index = $tr.index();
               var data = '{"bsLoginname":"'+loginName[index]+'"}';
                $.ajax({
                        url:"../bsUsers/selectPybsLoginname",
                        contentType:"application/json;charset=UTF-8",
                        data:data,
                        dataType:"json",
                        type:"post",
                        success:function (data) {
                            if(data.bsUserphone != ""){
                                 alert("购书人电话"+data.bsUserphone);
                            }else {
                                 alert("该用户已移除");
                            }
                        },
                         error:function (data) {
                             console.log(data);
                            alert("数据错误");
                         },
                    });
            });
        });
    </script>
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo">后台管理</div>
        <ul class="layui-nav layui-layout-left">
          <li class="layui-nav-item"><a href="../bsBooks/selectAll">书籍管理</a></li>
          <li class="layui-nav-item" style="display:none;"><a href="../bsBookclass/selectAll">书籍类别管理</a></li>
          <li class="layui-nav-item" style="display:none;"><a href="../bsPress/selectAll">出版社管理</a></li>
          <li class="layui-nav-item"><a href="../bsUsers/selectAll">用户管理</a></li>
          <li class="layui-nav-item"><a href="../bsOrder/adminList">订单管理</a></li>
        </ul>
        <ul class="layui-nav layui-layout-right">
            <li class="layui-nav-item">
                <a href="javascript:void(0);" onclick="logout()">退出登录</a>
            </li>
        </ul>
    </div>
    <div class="layui-body">
        <!-- 内容主体区域 -->
        <div class="layui-tab layui-tab-card">
            <ul class="layui-tab-title">
                <li class="layui-this">查看所有书籍</li>
            </ul>
            <div class="layui-tab-content" style="height: 800px;">
               <div id="selectAll" class="layui-tab-item layui-show">
                     <table id="demo" class="layui-table"  lay-skin="nob" lay-filter="test">
                        <colgroup>
                            <col width="100">
                            <col width="100">
                            <col width="200">
                            <col width="150">
                            <col width="150">
                            <col width="100">
                        </colgroup>
                        <thead>
                        <tr>
                            <th>图片</th>
                            <th>价格</th>
                            <th>链接</th>
                            <th>数量</th>
                            <th>省份</th>
                            <th>获取联系方式</th>
                        </tr>
                        </thead>
                        <tbody id="tbody">

                        </tbody>
                    </table>
                </div>
                <div class="layui-tab-item">2</div>
                <div class="layui-tab-item">3</div>
            </div>
        </div>
    </div>
</div>
<script>
// 退出登录
function logout() {
    layui.use('layer', function(){
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
</script>
</body>
</html>