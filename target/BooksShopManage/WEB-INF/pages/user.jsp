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
    <style>
        #image{
            width: 92px;
            height: 92px;
            margin: 10px;
            border: 0.5px solid black;
        }
    </style>
    <script>
        $(function () {
            layui.use(['element', 'form', 'laydate'], function(){
                var element = layui.element;
                var form = layui.form;
                var laydate = layui.laydate;
                
                // 初始化日期选择器
                laydate.render({
                    elem: '#birthday' //指定元素
                });
                laydate.render({
                    elem: '#addBirthday' //指定元素
                });
                
                // 存储用户数据
                var userData = [];
                
            var tbody = $("#tbody");
            var $tr;
            <c:forEach items="${bsUsersList}" var="b">
                    var bsUserid = "${b.bsUserid}";
                var bsLoginname = "${b.bsLoginname}";
                var bsUsersex = "${b.bsUsersex}";
                var bsTruename = "${b.bsTruename}";
                var bsUserbrithday = "${b.bsUserbrithday}";
                var bsUserqq = "${b.bsUserqq}";
                var bsUserphone = "${b.bsUserphone}";
                var bsUseremail = "${b.bsUseremail}";
                var bsProvince = "${b.bsProvince}";
                    
                    // 存储用户数据
                    userData.push({
                        bsUserid: bsUserid,
                        bsLoginname: bsLoginname,
                        bsUsersex: bsUsersex,
                        bsTruename: bsTruename,
                        bsUserbrithday: bsUserbrithday,
                        bsUserqq: bsUserqq,
                        bsUserphone: bsUserphone,
                        bsUseremail: bsUseremail,
                        bsProvince: bsProvince
                    });
                    
                $tr = $(" <tr>\n" +
                    "                            <td>"+bsLoginname+"</td>\n" +
                    "                            <td>"+bsUsersex+"</td>\n" +
                    "                            <td>"+bsTruename+"</td>\n" +
                    "                            <td>"+bsUserbrithday+"</td>\n" +
                    "                            <td>"+bsUserqq+"</td>\n" +
                    "                            <td>"+bsUserphone+"</td>\n" +
                    "                            <td>"+bsUseremail+"</td>\n" +
                    "                            <td>"+bsProvince+"</td>\n" +
                    "                <td><button class=\"layui-btn layui-btn-xs\" onclick=\"changePassword('"+bsUserid+"', '"+bsLoginname+"')\">修改密码</button>" +
                    "                <a class=\"layui-btn layui-btn-danger layui-btn-xs\" lay-event=\"del\">删除</a>" +
                    "            <input type=\"hidden\" value='${b.bsUserid}'></td>\n" +
                    "                        </tr>");
                tbody.append($tr);
            </c:forEach>
                
                // 加载用户选择下拉框
                loadUserSelect();
                
                function loadUserSelect() {
                    var $select = $('select[name="selectedUser"]');
                    $select.empty();
                    $select.append('<option value="">请选择要修改的用户</option>');
                    
                    userData.forEach(function(user) {
                        var displayText = user.bsLoginname + ' - ' + user.bsTruename;
                        $select.append('<option value="' + user.bsUserid + '">' + displayText + '</option>');
                    });
                    
                    // 重新渲染表单元素
                    form.render('select');
                    form.render('radio');
                }
                
                // 监听用户选择
                form.on('select(userSelect)', function(data){
                    var selectedUserId = data.value;
                    if(selectedUserId) {
                        // 根据选择的用户ID填充表单
                        var selectedUser = userData.find(function(user) {
                            return user.bsUserid == selectedUserId;
                        });
                        
                        if(selectedUser) {
                            // 填充表单数据
                            $('input[name="bsUserid"]').val(selectedUser.bsUserid);
                            $('input[name="bsLoginname"]').val(selectedUser.bsLoginname);
                            $('input[name="bsTruename"]').val(selectedUser.bsTruename);
                            $('input[name="bsUserqq"]').val(selectedUser.bsUserqq);
                            $('input[name="bsUserphone"]').val(selectedUser.bsUserphone);
                            $('input[name="bsUseremail"]').val(selectedUser.bsUseremail);
                            $('input[name="bsProvince"]').val(selectedUser.bsProvince);
                            $('input[name="bsUserbrithday1"]').val(selectedUser.bsUserbrithday);
                            
                            // 设置单选按钮
                            $('input[name="bsUsersex"][value="' + selectedUser.bsUsersex + '"]').prop('checked', true);
                            
                            form.render();
                        }
                    }
                });
                
                // 修改密码功能
                window.changePassword = function(userId, loginName) {
                    layui.use('layer', function(){
                        var layer = layui.layer;
                        layer.prompt({
                            formType: 1,
                            value: '',
                            title: '为用户 ' + loginName + ' 设置新密码',
                            area: ['300px', '150px']
                        }, function(value, index, elem){
                            if(value && value.trim() !== '') {
                                var data = '{"bsUserid":"'+userId+'","bsPassword":"'+value+'"}';
                                $.ajax({
                                    url:"../bsUsers/changePassword",
                                    contentType:"application/json;charset=UTF-8",
                                    data:data,
                                    dataType:"json",
                                    type:"post",
                                    success:function (result) {
                                        if(result > 0){
                                            layer.msg("密码修改成功", {icon: 1});
                                        } else {
                                            layer.msg("密码修改失败", {icon: 2});
                                        }
                                    },
                                    error:function () {
                                        layer.msg("请求失败", {icon: 2});
                                    }
                                });
                            } else {
                                layer.msg("密码不能为空", {icon: 2});
                            }
                            layer.close(index);
                        });
                    });
                };
                
                //监听修改用户表单提交
                form.on('submit(updateUser)', function(data){
                    data = data.field;
                    console.log(data);
                    data =
                        '{"bsUserid":'+data.bsUserid+',' +
                        '"bsLoginname":"'+data.bsLoginname+'",' +
                        '"bsUsersex":'+data.bsUsersex+',' +
                        '"bsTruename":"'+data.bsTruename+'",' +
                        '"bsUserbrithday1":"'+data.bsUserbrithday1+'",' +
                        '"bsUserqq":"'+data.bsUserqq+'",' +
                        '"bsUserphone":"'+data.bsUserphone+'",' +
                        '"bsUseremail":"'+data.bsUseremail+'",' +
                        '"bsProvince":"'+data.bsProvince+'"}';
                    console.log(data);
                      $.ajax({
                        url:"../bsUsers/updateOne",
                        contentType:"application/json;charset=UTF-8",
                        data:data,
                        dataType:"json",
                        type:"post",
                        success:function (data) {
                            if(data>0){
                                layer.alert("信息修改成功");
                                // 刷新页面以更新数据
                                location.reload();
                            }else if(data == 0){
                                layer.alert("该用户不存在或未完善信息");
                            }else{
                                layer.alert("信息修改失败");
                            }
                        },
                         error:function (data) {
                             console.log(data);
                            alert("数据错误");
                         },
                    });
                    return false;
                });
                
                //监听添加用户表单提交
                form.on('submit(addUser)', function(data){
                    data = data.field;
                    console.log(data);
                    data =
                        '{"bsLoginname":"'+data.bsLoginname+'",' +
                        '"bsPassword":"'+data.bsPassword+'",' +
                        '"bsUsersex":'+data.bsUsersex+',' +
                        '"bsTruename":"'+data.bsTruename+'",' +
                        '"bsUserbrithday1":"'+data.bsUserbrithday1+'",' +
                        '"bsUserqq":"'+data.bsUserqq+'",' +
                        '"bsUserphone":"'+data.bsUserphone+'",' +
                        '"bsUseremail":"'+data.bsUseremail+'",' +
                        '"bsProvince":"'+data.bsProvince+'"}';
                    console.log(data);
                      $.ajax({
                        url:"../bsUsers/insertOne",
                        contentType:"application/json;charset=UTF-8",
                        data:data,
                        dataType:"json",
                        type:"post",
                        success:function (data) {
                            if(data>0){
                                layer.alert("用户添加成功");
                                // 刷新页面以更新数据
                                location.reload();
                            }else{
                                layer.alert("用户添加失败");
                            }
                        },
                         error:function (data) {
                             console.log(data);
                            alert("数据错误");
                         },
                    });
                    return false;
                });
                
                form.verify({
                    //我们既支持上述函数式的方式，也支持下述数组的形式
                    //数组的两个值分别代表：[正则匹配、匹配不符时的提示文字]
                    bsUserid:[/^[0-9]*$/
                        ,"请输入正确的格式"]
                    ,
                    bsLoginname:[/^[a-zA-Z0-9_]{3,20}$/
                        ,"用户名只能包含字母、数字和下划线，长度3-20位"]
                    ,
                    bsUsersex:[/[0,1]/
                        ,"请填写"]
                    ,
                    bsTruename:[   /^[a-zA-Z\u4e00-\u9fa5]+$/
                        , "真实姓名只能包含中文和英文字母"
                    ]
                    ,
                    bsUserbrithday1:[ /^[1-9]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])$/
                        , "请输入正确的格式"
                    ]
                    ,
                    bsUserphone: [
                        /^1[3456789]\d{9}$/
                        ,'电话号有误'
                    ]
                });
                form.render();
            });
            
            // 删除用户功能
            $("#tbody").on('click','.layui-btn.layui-btn-danger.layui-btn-xs',function () {
                let bsUserid = $(this).next().val();
                let $tr = $(this).parent().parent();
               let index = $tr.index();
               var data = '{"bsUserid":"'+bsUserid+'"}';
                $.ajax({
                    url:"../bsUsers/delById",
                    contentType:"application/json;charset=UTF-8",
                    data:data,
                    dataType:"json",
                    type:"post",
                    success:function (data) {
                        if(data>0){
                            alert("成功删除");
                            $tr.remove();
                            // 重新加载用户选择下拉框
                            setTimeout(function() {
                                location.reload();
                            }, 500);
                        }
                    },
                     error:function (data) {
                        alert("数据错误");
                     },
                });
            });
        });
    </script>
    
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
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo">后台管理</div>
        <ul class="layui-nav layui-layout-left">
            <li class="layui-nav-item"><a href="../bsBooks/selectAll">书籍管理</a></li>
            <li class="layui-nav-item" style="display:none;"><a href="../bsBookclass/selectAll">书籍类别管理</a></li>
            <li class="layui-nav-item" style="display:none;"><a href="../bsPress/selectAll">出版社管理</a></li>
            <li class="layui-nav-item layui-this"><a href="../bsUsers/selectAll">用户管理</a></li>
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
                <li class="layui-this">查看所有用户</li>
                <li>修改用户信息</li>
                <li>添加新用户</li>
            </ul>
            <div class="layui-tab-content" style="height: 800px;">
                <div id="selectAll" class="layui-tab-item layui-show">
                     <table id="demo" class="layui-table"  lay-skin="nob" lay-filter="test">
                        <colgroup>
                            <col width="200">
                            <col width="150">
                            <col width="100">
                            <col width="200">
                            <col width="200">
                            <col width="150">
                            <col width="100">
                            <col width="200">
                            <col width="200">
                            <col width="150">
                        </colgroup>
                        <thead>
                        <tr>
                            <th>账号</th>
                            <th>用户性别：男1，女0</th>
                            <th>真实姓名</th>
                            <th>生日</th>
                            <th>qq</th>
                            <th>手机号</th>
                            <th>邮箱</th>
                            <th>省份</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody id="tbody">

                        </tbody>
                    </table>
                </div>
                <div class="layui-tab-item">
                    <form class="layui-form" lay-filter="test1" method="post">
                        <div class="layui-form-item">
                            <label class="layui-form-label">选择用户*</label>
                            <div class="layui-input-block">
                                <select name="selectedUser" lay-filter="userSelect" lay-verify="required" lay-search>
                                    <option value="">请选择要修改的用户</option>
                                </select>
                            </div>
                        </div>
                         <div class="layui-form-item">
                            <label class="layui-form-label">用户ID*</label>
                            <div class="layui-input-block">
                                <input type="number" name="bsUserid" lay-verify="bsUserid"
                                       placeholder="请输入"  autocomplete="off"
                                       class="layui-input" required readonly/>
                            </div>
                        </div>
                         <div class="layui-form-item">
                            <label class="layui-form-label">用户名*</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsLoginname" lay-verify="bsLoginname" placeholder="请输入"  autocomplete="off"
                                       class="layui-input" required/>
                            </div>
                        </div>
                          <div class="layui-form-item">
                            <label class="layui-form-label">性别*</label>
                            <div class="layui-input-block">
                                <input type="radio" lay-filter="bsUsersex" name="bsUsersex" value="1" title="男">
                                <input type="radio" lay-filter="bsUsersex" name="bsUsersex" value="0" title="女">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">真实姓名*</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsTruename" lay-verify="bsTruename" placeholder="请输入"  autocomplete="off"
                                       class="layui-input" required/>
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">生日*</label>
                            <div class="layui-inline">
                                <input type="text" name = "bsUserbrithday1"
                                       lay-verify="bsUserbrithday1" class="layui-input"
                                       id="birthday">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">QQ号</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsUserqq" placeholder="请输入QQ号" autocomplete="off"
                                       class="layui-input">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">手机号*</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsUserphone" lay-verify="bsUserphone" placeholder="请输入手机号" autocomplete="off" class="layui-input"  required >
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">邮箱</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsUseremail" placeholder="请输入邮箱" autocomplete="off"
                                       class="layui-input">
                            </div>
                        </div>
                          <div class="layui-form-item">
                            <label class="layui-form-label">所在省份</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsProvince" placeholder="请输入您所在的省份"
                                       autocomplete="off"
                                       class="layui-input">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <div class="layui-input-block">
                                <button type="submit" class="layui-btn" lay-submit lay-filter="updateUser">立即提交</button>
                                <button type="reset" class="layui-btn layui-btn-primary">重置</button>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="layui-tab-item">
                    <form class="layui-form" lay-filter="addUserForm" method="post">
                         <div class="layui-form-item">
                            <label class="layui-form-label">用户名*</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsLoginname" lay-verify="bsLoginname" placeholder="请输入用户名"  autocomplete="off"
                                       class="layui-input" required/>
                            </div>
                        </div>
                         <div class="layui-form-item">
                            <label class="layui-form-label">密码*</label>
                            <div class="layui-input-block">
                                <input type="password" name="bsPassword" lay-verify="required" placeholder="请输入密码"  autocomplete="off"
                                       class="layui-input" required/>
                            </div>
                        </div>
                         <div class="layui-form-item">
                            <label class="layui-form-label">性别*</label>
                            <div class="layui-input-block">
                                <input type="radio" lay-filter="addBsUsersex" name="bsUsersex" value="1" title="男">
                                <input type="radio" lay-filter="addBsUsersex" name="bsUsersex" value="0" title="女">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">真实姓名*</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsTruename" lay-verify="bsTruename" placeholder="请输入真实姓名"  autocomplete="off"
                                       class="layui-input" required/>
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">生日*</label>
                            <div class="layui-inline">
                                <input type="text" name = "bsUserbrithday1"
                                       lay-verify="bsUserbrithday1" class="layui-input"
                                       id="addBirthday">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">QQ号</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsUserqq" placeholder="请输入QQ号" autocomplete="off"
                                       class="layui-input">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">手机号*</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsUserphone" lay-verify="bsUserphone" placeholder="请输入手机号" autocomplete="off" class="layui-input"  required >
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">邮箱</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsUseremail" placeholder="请输入邮箱" autocomplete="off"
                                       class="layui-input">
                            </div>
                        </div>
                          <div class="layui-form-item">
                            <label class="layui-form-label">所在省份</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsProvince" placeholder="请输入您所在的省份"
                                       autocomplete="off"
                                       class="layui-input">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <div class="layui-input-block">
                                <button type="submit" class="layui-btn" lay-submit lay-filter="addUser">立即提交</button>
                                <button type="reset" class="layui-btn layui-btn-primary">重置</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>