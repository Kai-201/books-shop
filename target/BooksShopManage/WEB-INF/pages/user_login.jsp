<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>用户登录</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<script type="text/javascript" src="../static/js/jquery-2.2.4.min.js"></script>
<link href="../static/css/style.css" rel="stylesheet" type="text/css" media="all">
<script src="../static/layui/layui.js"></script>
<script type="text/javascript">
$(function () {
    $("#submit").click(function () {
        var username = $("input[name='username']").val();
        var password = $("input[name='password']").val();
        if(username == ""){
            layui.use('layer', function(){
                var layer = layui.layer;
                layer.alert("账号不能为空");
            });
        }else if(password == ""){
            layui.use('layer', function(){
                var layer = layui.layer;
                layer.alert("密码不能为空");
            });
        }else{
            var data = '{"bsLoginname":"'+username+'","bsLoginsecret":"'+password+'"}';
            $.ajax({
                url:"../bsUsers/login",
                contentType:"application/json;charset=UTF-8",
                data:data,
                dataType:"json",
                type:"post",
                success:function (data) {
                    if(data && data.bsUserid){
                        layui.use('layer', function(){
                            var layer = layui.layer;
                            layer.alert("登录成功");
                        });
                        setTimeout(function () { window.location = 'user_home.jsp'; }, 1000);
                    }else{
                        layui.use('layer', function(){
                            var layer = layui.layer;
                            layer.alert("用户名或密码错误，请重新输入");
                        });
                    }
                },
                error:function () {
                    layui.use('layer', function(){
                        var layer = layui.layer;
                        layer.alert("数据错误");
                    });
                }
            });
        }
        return false;
    });
});
</script>
</head>
<body>
<div class="main">
    <div class="w3_login">
        <div class="w3_login_module">
            <div class="module form-module">
                <div class="form" style="display: block;" class="layui-form" lay-filter="login">
                    <h2>用户登录</h2>
                    <form action="" method="post">
                        <input type="text" name="username" placeholder="用户名">
                        <input type="password" name="password" placeholder="密码">
                        <input type="submit" id="submit" lay-filter="*" value="登录">
                    </form>
                </div>
                <div class="cta"><a href="">忘记密码?</a></div>
            </div>
        </div>
    </div>
</div>
</body></html> 