package cn.lht.controller;

import cn.lht.entity.BsPassword;
import cn.lht.entity.BsUsers;
import cn.lht.service.BsPasswordService;
import cn.lht.service.BsUsersService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.lht.service.BsBooksService;
import cn.lht.entity.BsBooks;
import javax.servlet.http.HttpSession;

/**
 * (BsUsers)表控制层
 *
 * @author makejava
 * @since 2020-04-14 08:37:25
 */
@Controller
@RequestMapping("bsUsers")
public class BsUsersController {
    /**
     * 服务对象
     */
    @Resource
    private BsUsersService bsUsersService;
    /**
     * 服务对象
     */
    @Resource
    private BsPasswordService bsPasswordService;
    @Resource
    private BsBooksService bsBooksService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public BsUsers selectOne(Object id) {
        return this.bsUsersService.queryById(id);
    }
    /**
     *
     * @param model
     * @return 页面
     */
    @RequestMapping("selectAll")
    public String selectAll(Model model){
        List<BsUsers> bsUsersList = this.bsUsersService.selectAll();
        model.addAttribute("bsUsersList",bsUsersList);
        return "user";
    }
    /**
     * 通过用户名查询单条数据
     *
     * @param bsUsers 实例对象
     * @return 实例对象
     */
    @RequestMapping("selectPybsLoginname")
    public BsUsers selectPybsLoginname(@RequestBody BsUsers bsUsers){
        List<BsUsers> bsUsersList = this.bsUsersService.queryAll(bsUsers);
        if(bsUsersList.size() != 0){
          bsUsers = bsUsersList.get(0);
        }
        return bsUsers;
    }

    /**
     * 更新行
     *
     * @param bsUsers 实例对象
     * @return 影响行数
     */
    @RequestMapping("updateOne")
    @ResponseBody
    public Integer updateOne(@RequestBody BsUsers bsUsers) throws ParseException {
        Integer i= -1;
        Integer bsUserid = bsUsers.getBsUserid();
        BsUsers bsUsers1 = this.bsUsersService.queryById(bsUserid);
        if(bsUsers1 != null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(bsUsers.getBsUserbrithday1());
            java.sql.Date resultDate = new java.sql.Date(date.getTime());
            bsUsers.setBsUserbrithday(resultDate);
            i = this.bsUsersService.update(bsUsers);
        }else{
            i = 0;
        }
        return i;
    }

    /**
     * 删除行
     *
     * @param bsUsers 实例对象
     * @return 影响行数
     */
    @RequestMapping("delById")
    @ResponseBody
    public Integer delById(@RequestBody BsUsers bsUsers){
       Integer i = 0;
       boolean flog;
       Integer bsUserid = bsUsers.getBsUserid();
       bsUsers = this.bsUsersService.queryById(bsUserid);
       String bsLoginname = bsUsers.getBsLoginname();
       BsPassword bsPassword = new BsPassword();
       bsPassword.setBsLoginname(bsLoginname);
       List<BsPassword> bsPasswordList = this.bsPasswordService.queryAll(bsPassword);
       Integer bsUseridp = bsPasswordList.get(0).getBsUserid();
       flog = this.bsUsersService.deleteById(bsUserid);
       if(flog){
           i = 1;
           this.bsPasswordService.deleteById(bsUseridp);
       }
       return i;
    }

    /**
     * 添加新用户
     *
     * @param bsUsers 实例对象
     * @return 影响行数
     */
    @RequestMapping("insertOne")
    @ResponseBody
    public Integer insertOne(@RequestBody BsUsers bsUsers) throws ParseException {
        Integer i = 0;
        try {
            // 处理生日日期格式
            if (bsUsers.getBsUserbrithday1() != null && !bsUsers.getBsUserbrithday1().isEmpty()) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = simpleDateFormat.parse(bsUsers.getBsUserbrithday1());
                java.sql.Date resultDate = new java.sql.Date(date.getTime());
                bsUsers.setBsUserbrithday(resultDate);
            }
            
            // 为必填字段提供默认值
            if (bsUsers.getBsLoginsecret() == null || bsUsers.getBsLoginsecret().isEmpty()) {
                bsUsers.setBsLoginsecret(""); // 安全码默认为空字符串
            }
            if (bsUsers.getBsUserphoto() == null || bsUsers.getBsUserphoto().isEmpty()) {
                bsUsers.setBsUserphoto(""); // 用户头像默认为空字符串
            }
            if (bsUsers.getBsProvince() == null || bsUsers.getBsProvince().isEmpty()) {
                bsUsers.setBsProvince(""); // 省份默认为空字符串
            }
            
            // 插入用户信息
            BsUsers insertedUser = this.bsUsersService.insert(bsUsers);
            
            // 查询刚插入的用户获取用户ID
            List<BsUsers> userList = this.bsUsersService.queryAll(new BsUsers() {{ 
                setBsLoginname(bsUsers.getBsLoginname()); 
            }});
            
            if (userList != null && !userList.isEmpty()) {
                BsUsers newUser = userList.get(0);
                // 插入密码信息
                BsPassword bsPassword = new BsPassword();
                bsPassword.setBsLoginname(bsUsers.getBsLoginname());
                bsPassword.setBsPassword(bsUsers.getBsPassword());
                bsPassword.setBsUserid(newUser.getBsUserid());
                
                // 为BsPassword的必填字段提供默认值
                if (bsPassword.getBsCreatetime() == null || bsPassword.getBsCreatetime().isEmpty()) {
                    bsPassword.setBsCreatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                }
                if (bsPassword.getBsLastlogintime() == null || bsPassword.getBsLastlogintime().isEmpty()) {
                    bsPassword.setBsLastlogintime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                }
                if (bsPassword.getBsLastloginip() == null || bsPassword.getBsLastloginip().isEmpty()) {
                    bsPassword.setBsLastloginip("0:0:0:0:0:0:0:1");
                }
                
                this.bsPasswordService.insert(bsPassword);
                i = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            i = 0;
        }
        return i;
    }

    /**
     * 用户登录接口
     * @param bsUsers 用户对象（只需用户名和密码）
     * @return 登录成功返回用户信息，失败返回null
     */
    @RequestMapping("login")
    @ResponseBody
    public BsUsers login(@RequestBody BsUsers bsUsers, HttpSession session) {
        // 先查密码表
        String loginName = bsUsers.getBsLoginname();
        String password = String.valueOf(bsUsers.getBsLoginsecret());
        BsPassword pwd = bsPasswordService.queryByLogin(loginName, password);
        if (pwd != null) {
            // 密码正确，查用户表
            List<BsUsers> userList = bsUsersService.queryAll(new BsUsers() {{ setBsLoginname(loginName); }});
            if (userList != null && !userList.isEmpty()) {
                BsUsers user = userList.get(0);
                session.setAttribute("user", user);
                return user;
            }
        }
        return null;
    }

    /**
     * 用户主页面跳转
     */
    @RequestMapping("home")
    public String userHome(Model model, HttpSession session) {
        BsUsers user = (BsUsers) session.getAttribute("user");
        if (user == null) {
            return "redirect:../index.jsp";
        }
        
        // 显示所有书籍，让用户可以浏览和购买
        List<BsBooks> bsBooksList = bsBooksService.queryAll(new BsBooks());
        
        model.addAttribute("bsBooksList", bsBooksList);
        model.addAttribute("userId", user.getBsUserid());
        return "user_home";
    }

    /**
     * 修改用户密码
     * @param request 包含用户ID和新密码的请求
     * @return 修改结果
     */
    @RequestMapping("changePassword")
    @ResponseBody
    public Integer changePassword(@RequestBody Map<String, Object> request) {
        try {
            Integer userId = Integer.valueOf(request.get("bsUserid").toString());
            String newPassword = request.get("bsPassword").toString();
            
            // 查询用户是否存在
            BsUsers user = this.bsUsersService.queryById(userId);
            if (user == null) {
                return 0; // 用户不存在
            }
            
            // 查询密码记录
            BsPassword passwordQuery = new BsPassword();
            passwordQuery.setBsLoginname(user.getBsLoginname());
            List<BsPassword> passwordList = this.bsPasswordService.queryAll(passwordQuery);
            
            if (passwordList == null || passwordList.isEmpty()) {
                return 0; // 密码记录不存在
            }
            
            // 更新密码
            BsPassword password = passwordList.get(0);
            password.setBsPassword(newPassword);
            BsPassword updatedPassword = this.bsPasswordService.update(password);
            
            return updatedPassword != null ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 用户退出登录
     */
    @RequestMapping("logout")
    @ResponseBody
    public Integer logout(HttpSession session) {
        try {
            session.invalidate(); // 清除session
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}