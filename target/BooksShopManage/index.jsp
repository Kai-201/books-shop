<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登录</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<script type="text/javascript" src="static/js/jquery-2.2.4.min.js"></script>
<link href="static/css/style.css" rel="stylesheet" type="text/css" media="all">
<script src="static/layui/layui.js"></script>
<script type="text/javascript">
$(function () {
	// 切换登录类型
	$("#switchType").click(function(){
		var type = $(this).attr("data-type");
		if(type=="admin"){
			$(this).attr("data-type","user").text("切换为管理员登录");
			$("#loginTitle").text("用户登录");
			$("#loginForm").attr("data-type","user");
		}else{
			$(this).attr("data-type","admin").text("切换为用户登录");
			$("#loginTitle").text("管理员登录");
			$("#loginForm").attr("data-type","admin");
		}
	});
	$("#submit").click(function () {
		var username = $("input[name='username']").val();
		var password = $("input[name='password']").val();
		var type = $("#loginForm").attr("data-type");
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
			var url, data;
			if(type=="admin"){
				url = "bsAdmin/selectBybsAdminlogin";
				data = '{"bsAdminlogin":"'+username+'","bsAdminpass":"'+password+'"}';
			}else{
				url = "bsUsers/login";
				data = '{"bsLoginname":"'+username+'","bsLoginsecret":"'+password+'"}';
			}
			$.ajax({
				url:url,
				contentType:"application/json;charset=UTF-8",
				data:data,
				dataType:"json",
				type:"post",
				success:function (data) {
					if(type=="admin"){
						if(data>0){
							layui.use('layer', function(){
								var layer = layui.layer;
								layer.msg("管理员登录成功", {icon: 1, time: 1000});
							});
							setTimeout(function () { window.location = 'bsBooks/selectAll'; }, 1000);
						}else{
							layui.use('layer', function(){
								var layer = layui.layer;
								layer.alert("用户名或密码错误，请重新输入");
							});
						}
					}else{
						if(data && data.bsUserid){
							layui.use('layer', function(){
								var layer = layui.layer;
								layer.msg("用户登录成功", {icon: 1, time: 1000});
							});
							setTimeout(function () { window.location = 'bsUsers/home'; }, 1000);
						}else{
							layui.use('layer', function(){
								var layer = layui.layer;
								layer.alert("用户名或密码错误，请重新输入");
							});
						}
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
				<div class="form" style="display: block;" class="layui-form" id="loginForm" data-type="admin" lay-filter="login">
					<h2 id="loginTitle">管理员登录</h2>
					<form action="" method="post">
						<input type="text" name="username" placeholder="用户名">
						<input type="password" name="password" placeholder="密码">
						<input type="submit" id="submit" lay-filter="*" value="登录">
					</form>
				</div>
				<div class="cta"><a href="javascript:void(0);" id="switchType" data-type="admin">切换为用户登录</a></div>
			</div>
		</div>
	</div>
</div>
</body></html>