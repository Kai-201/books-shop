<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>后台管理</title>
    <link rel="stylesheet" href="../static/layui/css/layui.css">
    <link rel="stylesheet" href="../static/layui/css/custom-theme.css">
    <script src="../static/js/jquery-3.3.1.min.js"></script>
    <script src="../static/layui/layui.js"></script>
    <script>
        $(function () {
            layui.use('element', function(){
                var element = layui.element;

            });
            var tbody = $("#tbody");
            var loginName;
            var press = [];
            var a = 0;
            <c:forEach items="${bsPressList}" var="b">
                press[a] = "${b.bsPressname}";
                a++;
            </c:forEach>
            a = 0;
             let $tr;
            <c:forEach items="${bsBooksList}" var="b">
                var url = "${b.bsBookcover}";
                var isbn = "${b.bsBooksn}"
                var bookname = "${b.bsBookname}";
                var price = "${b.bsBookprice}";
                var bookId = "${b.bsBookid}";
                var author = "${b.bsBookauthor}";
                var classId = "${b.bsBookclassid}";
                var pressNum = "${b.bsPressnum}";
                var province = "${b.bsProvince}";
                var bookNum = "${b.bsBooksnum}";
                var bookBt = "${b.bsBookbt}";
                var uploaderName = "${b.bsUploadername}" || "未知用户";
                
                $tr = $("<tr>\n" +
                "                <td>\n" +
                "                    <span class = \"image\"><img src="+url+" alt=\"\"></span>\n" +
                "                    <span>\n" +
                "                        <h6 class = \"bookName\">"+bookname+"</h6>\n" +
                "                        <span class=\"press\">"+press[a]+"</span>\n" +
                "                    </span>\n" +
                "                </td>\n" +
                "                <td>￥ "+price+"</td>\n" +
                "                <td>"+isbn+"</td>\n" +
                "                <td>"+uploaderName+"</td>\n" +
                "                <td>"+bookNum+"</td>\n" +
                "                <td>\n" +
                "                    <button class=\"layui-btn layui-btn-xs\" onclick=\"editBook('"+bookId+"', '"+bookname+"', '"+author+"', '"+classId+"', '"+pressNum+"', '"+price+"', '"+url+"', '"+province+"', '"+bookNum+"', '"+bookBt+"', '"+isbn+"')\">修改</button>\n" +
                "                    <button class=\"layui-btn layui-btn-danger layui-btn-xs\" onclick=\"deleteBook('"+bookId+"', this)\">删除</button>\n" +
                "                </td>\n" +
                "            </tr>");
                tbody.append($tr);
                a++;
            </c:forEach>
             $("#tbody").on('click','.layui-btn.layui-btn-danger.layui-btn-xs',function () {
                let bookId = $(this).next().val();
                let $tr = $(this).parent().parent();
               let index = $tr.index();
               alert(bookId);
               var data = '{"bsBookid":"'+bookId+'"}';
                $.ajax({
                    url:"../bsBooks/delById",
                    contentType:"application/json;charset=UTF-8",
                    data:data,
                    dataType:"json",
                    type:"post",
                    success:function (data) {
                        if(data>0){
                            alert("成功删除");
                            $tr.remove();
                        }
                    },
                     error:function (data) {
                        alert("数据错误");
                     },
                });
            });
            
            // 修改书籍函数
            window.editBook = function(bookId, bookname, author, classId, pressNum, price, cover, province, bookNum, bookBt, isbn) {
                // 填充表单数据
                $('input[name="edit_bsBookid"]').val(bookId);
                $('input[name="edit_bsBooksn"]').val(isbn);
                $('input[name="edit_bsBookname"]').val(bookname);
                $('input[name="edit_bsBookauthor"]').val(author);
                $('input[name="edit_bsBookclassid"]').val(classId);
                $('input[name="edit_bsPressnum"]').val(pressNum);
                $('input[name="edit_bsBookprice"]').val(price);
                $('input[name="edit_bsProvince"]').val(province);
                $('input[name="edit_bsBooksnum"]').val(bookNum);
                $('input[name="edit_bsBookbt"]').val(bookBt);
                $('#edit_image').attr('src', cover);
                layui.form.render();
                // 弹窗显示修改表单（用jQuery对象，关闭后还原）
                var $modal = $('.edit-book-modal');
                layer.open({
                    type: 1,
                    title: '修改书籍',
                    area: ['600px', '600px'],
                    content: $modal.show(),
                    end: function() {
                        // 关闭弹窗后把表单还原回页面底部
                        $('body').append($modal.hide());
                    }
                });
            };
            
            // 删除书籍函数
            window.deleteBook = function(bookId, btn) {
                let $tr = $(btn).parent().parent();
                var data = '{"bsBookid":"'+bookId+'"}';
                $.ajax({
                    url:"../bsBooks/delById",
                    contentType:"application/json;charset=UTF-8",
                    data:data,
                    dataType:"json",
                    type:"post",
                    success:function (data) {
                        if(data>0){
                            layer.msg("成功删除", {icon: 1});
                            $tr.remove();
                        } else {
                            layer.msg("删除失败", {icon: 2});
                        }
                    },
                     error:function (data) {
                        layer.msg("数据错误", {icon: 2});
                     },
                });
            };
            
             //上传图片
            var url = 'https://api.uomg.com/api/image.ali';
            var imgUrl = null;
             $("input[type='file']").change(function(e) {
                file_upload(this.files)
            });
            
            // 修改书籍的图片上传
            $("input[name='edit_bsBookcover']").change(function(e) {
                edit_file_upload(this.files);
            });
            
            var obj = $('body');
            obj.on('dragenter', function(e) {
                e.stopPropagation();
                e.preventDefault()
            });
            obj.on('dragover', function(e) {
                e.stopPropagation();
                e.preventDefault()
            });
            obj.on('drop', function(e) {
                e.preventDefault();
                file_upload(e.originalEvent.dataTransfer.files)
            });
             function file_upload(files){
                if (files.length == 0) return alert('请选择图片文件！');
                for(var j = 0,len = files.length; j < len; j++){
                    console.log(files[j]);
                    let imageData = new FormData();
                    imageData.append("file", 'multipart');
                    imageData.append("Filedata", files[j]);
                    $.ajax({
                        url: url,
                        type: 'POST',
                        data: imageData,
                        cache: false,
                        contentType: false,
                        processData: false,
                        dataType: 'json',
                        // 图片上传成功
                        success: function (result) {
                            if (result.code == 1){
                                imgUrl = result.imgurl;
                                $("#image").attr('src',imgUrl);
                            }else{
                                alert("上传失败，请重新上传");
                            }
                        },
                        // 图片上传失败
                        error: function () {
                            console.log('图片上传失败');
                        }
                    });
                }
            }
            
            // 修改书籍的图片上传函数
            function edit_file_upload(files){
                if (files.length == 0) return alert('请选择图片文件！');
                for(var j = 0,len = files.length; j < len; j++){
                    console.log(files[j]);
                    let imageData = new FormData();
                    imageData.append("file", 'multipart');
                    imageData.append("Filedata", files[j]);
                    $.ajax({
                        url: url,
                        type: 'POST',
                        data: imageData,
                        cache: false,
                        contentType: false,
                        processData: false,
                        dataType: 'json',
                        // 图片上传成功
                        success: function (result) {
                            if (result.code == 1){
                                imgUrl = result.imgurl;
                                $("#edit_image").attr('src',imgUrl);
                            }else{
                                alert("上传失败，请重新上传");
                            }
                        },
                        // 图片上传失败
                        error: function () {
                            console.log('图片上传失败');
                        }
                    });
                }
            }
            
            //提交按钮
            layui.use('form', function(){
                var form = layui.form;
                form.render();
                //监听提交
                form.on('submit', function(data,e){
                    //var data = JSON.stringify(form.val("test1"));
                    data = data.field;
                    let img = $("#image").attr('src') || '';
                    data.bsBookcover = img;
                    console.log(data);
                    
                    // 处理空字段，为NOT NULL字段提供默认值
                    var bookData = {
                        bsBooksn: data.bsBooksn || 'BK001',
                        bsBookname: data.bsBookname || '',
                        bsBookauthor: data.bsBookauthor || '未知作者',
                        bsBookclassid: data.bsBookclassid || 103,
                        bsBookbt1: data.bsBookbt || '',
                        bsPressnum: data.bsPressnum || 'PUB001',
                        bsBookprice: data.bsBookprice || 0,
                        bsBookcover: data.bsBookcover || 'https://s2.ax1x.com/2020/03/05/37nV6P.jpg',
                        bsProvince: data.bsProvince || '湖南',
                        bsBooksnum: data.bsBooksnum || 0,
                        bsUploaderid: 1,
                        bsUploadername: '管理员'
                    };
                    
                    // 验证必填字段
                    if (!bookData.bsBookname || bookData.bsBookname.trim() === '') {
                        layer.msg('图书名称不能为空', {icon: 2});
                        return false;
                    }
                    if (!bookData.bsBookprice || bookData.bsBookprice <= 0) {
                        layer.msg('图书价格必须大于0', {icon: 2});
                        return false;
                    }
                    if (!bookData.bsBooksnum || bookData.bsBooksnum <= 0) {
                        layer.msg('图书数量必须大于0', {icon: 2});
                        return false;
                    }
                    
                    data = JSON.stringify(bookData);
                    console.log(data);
                      $.ajax({
                        url:"insertBook",
                        contentType:"application/json;charset=UTF-8",
                        data:data,
                        dataType:"json",
                        type:"post",
                        success:function (data) {
                            console.log("成功响应:", data);
                            if(data>0){
                                layer.alert("书籍上传成功");
                            }else{
                                layer.alert("书籍上传失败");
                            }
                        },
                         error:function (xhr, status, error) {
                             console.log("错误详情:", xhr.responseText);
                             console.log("状态:", status);
                             console.log("错误:", error);
                            alert("数据错误: " + xhr.responseText);
                         },
                    });
                    return false;
                });
                // 移除form.verify验证规则，让修改表单不受限制
                // form.verify({
                //     // 注释掉所有验证规则
                // });
                form.render();
            });
              //日期函数
            layui.use('laydate', function(){
                var laydate = layui.laydate;
                //执行一个laydate实例
                laydate.render({
                    elem: '#test1'//指定元素
                });
                // 修改书籍的日期选择器
                laydate.render({
                    elem: '#edit_test1'//指定元素
                });
            });
            
            // 修改书籍表单提交
            layui.use('form', function(){
                var form = layui.form;
                form.on('submit(updateBook)', function(data){
                    // 直接获取表单数据，不进行验证
                    var formData = data.field;
                    let img = $("#edit_image").attr('src') || '';
                    
                    // 处理空字段，为NOT NULL字段提供默认值
                    var bookData = {
                        bsBookid: formData.edit_bsBookid,
                        bsBooksn: formData.edit_bsBooksn || 'BK001',
                        bsBookname: formData.edit_bsBookname || '',
                        bsBookauthor: formData.edit_bsBookauthor || '未知作者',
                        bsBookclassid: formData.edit_bsBookclassid || 103,
                        bsBookbt1: formData.edit_bsBookbt || '',
                        bsPressnum: formData.edit_bsPressnum || 'PUB001',
                        bsBookprice: formData.edit_bsBookprice || 0,
                        bsBookcover: img || 'https://s2.ax1x.com/2020/03/05/37nV6P.jpg',
                        bsProvince: formData.edit_bsProvince || '湖南',
                        bsBooksnum: formData.edit_bsBooksnum || 0,
                        bsUploaderid: 1,
                        bsUploadername: '管理员'
                    };
                    
                    // 只验证必填字段
                    if (!bookData.bsBookname || bookData.bsBookname.trim() === '') {
                        layer.msg('图书名称不能为空', {icon: 2});
                        return false;
                    }
                    if (!bookData.bsBookprice || bookData.bsBookprice <= 0) {
                        layer.msg('图书价格必须大于0', {icon: 2});
                        return false;
                    }
                    if (!bookData.bsBooksnum || bookData.bsBooksnum <= 0) {
                        layer.msg('图书数量必须大于0', {icon: 2});
                        return false;
                    }
                    
                    var jsonData = JSON.stringify(bookData);
                    console.log("修改书籍数据:", jsonData);
                    
                    $.ajax({
                        url:"updateBook",
                        contentType:"application/json;charset=UTF-8",
                        data:jsonData,
                        dataType:"json",
                        type:"post",
                        success:function (result) {
                            console.log("修改响应:", result);
                            if(result > 0){
                                layer.msg("书籍修改成功", {icon: 1});
                                setTimeout(function() {
                                    location.reload();
                                }, 1000);
                            }else{
                                layer.msg("书籍修改失败", {icon: 2});
                            }
                        },
                        error:function (xhr, status, error) {
                            console.log("修改错误详情:", xhr.responseText);
                            layer.msg("修改请求失败: " + xhr.responseText, {icon: 2});
                        },
                    });
                    return false;
                });
            });
            
            // 完全绕过layui验证的修改书籍提交函数
            window.submitUpdateBook = function() {
                // 直接获取表单数据
                var formData = {
                    edit_bsBookid: $('input[name="edit_bsBookid"]').val(),
                    edit_bsBooksn: $('input[name="edit_bsBooksn"]').val(),
                    edit_bsBookname: $('input[name="edit_bsBookname"]').val(),
                    edit_bsBookauthor: $('input[name="edit_bsBookauthor"]').val(),
                    edit_bsBookclassid: $('input[name="edit_bsBookclassid"]').val(),
                    edit_bsBookbt: $('input[name="edit_bsBookbt"]').val(),
                    edit_bsPressnum: $('input[name="edit_bsPressnum"]').val(),
                    edit_bsBookprice: $('input[name="edit_bsBookprice"]').val(),
                    edit_bsProvince: $('input[name="edit_bsProvince"]').val(),
                    edit_bsBooksnum: $('input[name="edit_bsBooksnum"]').val()
                };
                let img = $("#edit_image").attr('src') || '';
                var bookData = {
                    bsBookid: formData.edit_bsBookid,
                    bsBooksn: formData.edit_bsBooksn || 'BK001',
                    bsBookname: formData.edit_bsBookname || '',
                    bsBookauthor: formData.edit_bsBookauthor || '未知作者',
                    bsBookclassid: formData.edit_bsBookclassid || 103,
                    bsBookbt1: formData.edit_bsBookbt || '',
                    bsPressnum: formData.edit_bsPressnum || 'PUB001',
                    bsBookprice: formData.edit_bsBookprice || 0,
                    bsBookcover: img || 'https://s2.ax1x.com/2020/03/05/37nV6P.jpg',
                    bsProvince: formData.edit_bsProvince || '湖南',
                    bsBooksnum: formData.edit_bsBooksnum || 0,
                    bsUploaderid: 1,
                    bsUploadername: '管理员'
                };
                if (!bookData.bsBookname || bookData.bsBookname.trim() === '') {
                    layer.msg('图书名称不能为空', {icon: 2});
                    return;
                }
                if (!bookData.bsBookprice || bookData.bsBookprice <= 0) {
                    layer.msg('图书价格必须大于0', {icon: 2});
                    return;
                }
                if (!bookData.bsBooksnum || bookData.bsBooksnum <= 0) {
                    layer.msg('图书数量必须大于0', {icon: 2});
                    return;
                }
                var jsonData = JSON.stringify(bookData);
                console.log("修改书籍数据:", jsonData);
                $.ajax({
                    url:"updateBook",
                    contentType:"application/json;charset=UTF-8",
                    data:jsonData,
                    dataType:"json",
                    type:"post",
                    success:function (result) {
                        console.log("修改响应:", result);
                        if(result > 0){
                            layer.msg("书籍修改成功", {icon: 1});
                            setTimeout(function() {
                                location.reload();
                            }, 1000);
                        }else{
                            layer.msg("书籍修改失败", {icon: 2});
                        }
                    },
                    error:function (xhr, status, error) {
                        console.log("修改错误详情:", xhr.responseText);
                        layer.msg("修改请求失败: " + xhr.responseText, {icon: 2});
                    },
                });
            };
        });
    </script>
</head>
<body class="layui-layout-body">
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo">后台管理</div>
        <ul class="layui-nav layui-layout-left">
            <li class="layui-nav-item layui-this"><a href="../bsBooks/selectAll">书籍管理</a></li>
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
                <li>添加书籍</li>
            </ul>
            <div class="layui-tab-content" style="height: 800px;">
                <div id="selectAll" class="layui-tab-item layui-show">
                     <table id="demo" class="layui-table"  lay-skin="nob" lay-filter="test">
                        <colgroup>
                            <col width="180">
                            <col width="120">
                            <col width="120">
                            <col width="100">
                            <col width="100">
                            <col width="120">
                        </colgroup>
                        <thead>
                        <tr>
                            <th>图片</th>
                            <th>价格</th>
                            <th>ISBN</th>
                            <th>上传者</th>
                            <th>剩余数量</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody id="tbody"></tbody>
                    </table>
                </div>
                <div class="layui-tab-item">
                    <form class="layui-form" lay-filter="test1" method="post">
                        <div class="layui-form-item">
                            <label class="layui-form-label">图书编号</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsBooksn"
                                       placeholder="请输入图书编号"  autocomplete="off"
                                       class="layui-input"/>
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">图书名称*</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsBookname"
                                       placeholder="请输入图书名称"  autocomplete="off"
                                       class="layui-input" required/>
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">图书作者</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsBookauthor"
                                       placeholder="请输入图书作者"
                                       autocomplete="off" class="layui-input">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">图书类别id</label>
                            <div class="layui-input-block">
                                <input type="number" name="bsBookclassid"
                                       placeholder="请输入图书类别id" autocomplete="off"
                                       class="layui-input">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">图书出版日期</label>
                            <div class="layui-inline"> <!-- 注意：这一层元素并不是必须的 -->
                                <input type="text" name = "bsBookbt"
                                       class="layui-input" id="test1">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">图书出版社编号</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsPressnum"
                                       placeholder="请输入出版社编号"  autocomplete="off"
                                       class="layui-input"/>
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">图书价格*</label>
                            <div class="layui-input-block">
                                <input type="number" name="bsBookprice" step="0.01" placeholder="请输入" autocomplete="off" class="layui-input" required>
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <img id="image" name = "image" lay-verify="image" src="" alt="">
                            <label class="layui-form-label">图书照片</label>
                            <div class="layui-input-block">
                                <input type="file" name = "bsBookcover" value="选择图片" accept="image/*" multiple="">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">所在省份</label>
                            <div class="layui-input-block">
                                <input type="text" name="bsProvince" placeholder="请输入" autocomplete="off" class="layui-input">
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <label class="layui-form-label">书籍数量*</label>
                            <div class="layui-input-block">
                                <input type="number" name="bsBooksnum" placeholder="请输入" autocomplete="off" class="layui-input" required>
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <div class="layui-input-block">
                                <button type="submit" class="layui-btn" lay-submit lay-filter="*">立即提交</button>
                                <button type="reset" class="layui-btn layui-btn-primary">重置</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- 修改书籍弹窗表单 -->
<div class="edit-book-modal" style="display:none;">
    <form class="layui-form" lay-filter="editForm" method="post" style="padding:24px 24px 0 24px;">
        <input type="hidden" name="edit_bsBookid">
        <div class="layui-form-item">
            <label class="layui-form-label">图书编号</label>
            <div class="layui-input-block">
                <input type="text" name="edit_bsBooksn" placeholder="请输入图书编号" autocomplete="off" class="layui-input"/>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">图书名称*</label>
            <div class="layui-input-block">
                <input type="text" name="edit_bsBookname" placeholder="请输入图书名称" autocomplete="off" class="layui-input" required/>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">图书作者</label>
            <div class="layui-input-block">
                <input type="text" name="edit_bsBookauthor" placeholder="请输入图书作者" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">图书类别id</label>
            <div class="layui-input-block">
                <input type="number" name="edit_bsBookclassid" placeholder="请输入图书类别id" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">图书出版社编号</label>
            <div class="layui-input-block">
                <input type="text" name="edit_bsPressnum" placeholder="请输入出版社编号" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">图书价格*</label>
            <div class="layui-input-block">
                <input type="number" name="edit_bsBookprice" placeholder="请输入价格" autocomplete="off" class="layui-input" required>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">所在省份</label>
            <div class="layui-input-block">
                <input type="text" name="edit_bsProvince" placeholder="请输入" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">书籍数量*</label>
            <div class="layui-input-block">
                <input type="number" name="edit_bsBooksnum" placeholder="请输入" autocomplete="off" class="layui-input" required>
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">简介</label>
            <div class="layui-input-block">
                <input type="text" name="edit_bsBookbt" placeholder="请输入简介" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">封面</label>
            <div class="layui-input-block">
                <img id="edit_image" name="edit_image" src="" alt="" style="width: 100px; height: 120px; margin: 10px;">
                <input type="file" name="edit_bsBookcover" value="选择图片" accept="image/*" multiple="">
            </div>
        </div>
        <div class="layui-form-item">
            <div class="layui-input-block">
                <button type="button" class="layui-btn" onclick="submitUpdateBook()">立即修改</button>
                <button type="reset" class="layui-btn layui-btn-primary">重置</button>
            </div>
        </div>
    </form>
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