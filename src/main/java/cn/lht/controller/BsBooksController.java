package cn.lht.controller;

import cn.lht.entity.BsBooks;
import cn.lht.entity.BsPress;
import cn.lht.entity.BsUsers;
import cn.lht.service.BsBooksService;
import cn.lht.service.BsPressService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * (BsBooks)表控制层
 *
 * @author makejava
 * @since 2020-04-13 23:07:10
 */
@Controller
@RequestMapping("bsBooks")
public class BsBooksController {
    /**
     * 服务对象
     */
    @Resource
    private BsBooksService bsBooksService;
    /**
     * 服务对象
     */
    @Resource
    private BsPressService bsPressService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public BsBooks selectOne(Integer id) {
        return this.bsBooksService.queryById(id);
    }

    /**
     * 查询所有书籍
     *
     * @param model
     * @return 字符串
     */
    @RequestMapping("selectAll")
    public String selectAll(Model model){
        List<BsBooks> bsBooksList = this.bsBooksService.queryAllByLimit(0,10);
        List<BsPress> bsPressList = new ArrayList<BsPress>();
        BsPress bsPress = new BsPress();
        for (BsBooks b : bsBooksList) {
            bsPress.setBsPressnum(b.getBsPressnum());
            List<BsPress> bsPressList1 = this.bsPressService.queryAll(bsPress);
            if (bsPressList1 != null && !bsPressList1.isEmpty()) {
                bsPressList.add(bsPressList1.get(0));
            } else {
                BsPress defaultPress = new BsPress();
                defaultPress.setBsPressname("未知出版社");
                defaultPress.setBsPressnum(b.getBsPressnum());
                bsPressList.add(defaultPress);
            }
        }
        model.addAttribute("bsBooksList",bsBooksList);
        model.addAttribute("bsPressList",bsPressList);
        return "homepage";
    }
    @RequestMapping("delById")
    @ResponseBody
    public Integer delById(@RequestBody BsBooks bsBooks){
        int i = 0;
        Integer bookId = bsBooks.getBsBookid();
        boolean flog = this.bsBooksService.deleteById(bookId);
        if(flog){
            i = 1;
        }
        return i;
    }

    /**
     *
     * @param bsBooks
     * @return
     * @throws ParseException
     */
    @RequestMapping("insertBook")
    @ResponseBody
    public Integer insertBook(@RequestBody BsBooks bsBooks) throws ParseException {
        System.out.println("接收到的数据: " + bsBooks);
        
        // 处理出版日期，如果为空则使用当前日期
        if (bsBooks.getBsBookbt1() != null && !bsBooks.getBsBookbt1().trim().isEmpty()) {
            System.out.println("处理出版日期: " + bsBooks.getBsBookbt1());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(bsBooks.getBsBookbt1());
            java.sql.Date resultDate = new java.sql.Date(date.getTime());
            bsBooks.setBsBookbt(resultDate);
        } else {
            // 如果出版日期为空，使用当前日期
            System.out.println("出版日期为空，使用当前日期");
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            bsBooks.setBsBookbt(currentDate);
        }
        
        System.out.println("准备插入数据库的数据: " + bsBooks);
        
        Integer i = 0;
        try {
            i = this.bsBooksService.insert(bsBooks);
            System.out.println("插入结果: " + i);
        } catch (Exception e) {
            System.out.println("插入失败，异常信息: " + e.getMessage());
            e.printStackTrace();
        }
        return i;
    }
    
    /**
     * 修改书籍
     * @param bsBooks
     * @return
     * @throws ParseException
     */
    @RequestMapping("updateBook")
    @ResponseBody
    public Integer updateBook(@RequestBody BsBooks bsBooks) throws ParseException {
        System.out.println("接收到的修改数据: " + bsBooks);
        
        // 处理出版日期，如果为空则使用当前日期
        if (bsBooks.getBsBookbt1() != null && !bsBooks.getBsBookbt1().trim().isEmpty()) {
            System.out.println("处理出版日期: " + bsBooks.getBsBookbt1());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(bsBooks.getBsBookbt1());
            java.sql.Date resultDate = new java.sql.Date(date.getTime());
            bsBooks.setBsBookbt(resultDate);
        } else {
            // 如果出版日期为空，使用当前日期
            System.out.println("出版日期为空，使用当前日期");
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            bsBooks.setBsBookbt(currentDate);
        }
        
        System.out.println("准备更新数据库的数据: " + bsBooks);
        
        Integer i = 0;
        try {
            BsBooks updatedBook = this.bsBooksService.update(bsBooks);
            if (updatedBook != null) {
                i = 1;
            }
            System.out.println("更新结果: " + i);
        } catch (Exception e) {
            System.out.println("更新失败，异常信息: " + e.getMessage());
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 用户上传书籍
     * @param bsBooks
     * @return
     * @throws ParseException
     */
    @RequestMapping("userInsertBook")
    @ResponseBody
    public Integer userInsertBook(@RequestBody BsBooks bsBooks, HttpSession session) throws ParseException {
        System.out.println("用户上传书籍数据: " + bsBooks);
        
        // 从session获取用户信息
        BsUsers currentUser = (BsUsers) session.getAttribute("user");
        if (currentUser == null) {
            System.out.println("用户未登录");
            return 0;
        }
        
        // 设置上传者信息
        bsBooks.setBsUploaderid(currentUser.getBsUserid());
        bsBooks.setBsUploadername(currentUser.getBsUsername());
        
        // 处理出版日期，如果为空则使用当前日期
        if (bsBooks.getBsBookbt1() != null && !bsBooks.getBsBookbt1().trim().isEmpty()) {
            System.out.println("处理出版日期: " + bsBooks.getBsBookbt1());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(bsBooks.getBsBookbt1());
            java.sql.Date resultDate = new java.sql.Date(date.getTime());
            bsBooks.setBsBookbt(resultDate);
        } else {
            // 如果出版日期为空，使用当前日期
            System.out.println("出版日期为空，使用当前日期");
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            bsBooks.setBsBookbt(currentDate);
        }
        
        System.out.println("准备插入数据库的数据: " + bsBooks);
        
        Integer i = 0;
        try {
            i = this.bsBooksService.insert(bsBooks);
            System.out.println("用户上传书籍结果: " + i);
        } catch (Exception e) {
            System.out.println("用户上传书籍失败，异常信息: " + e.getMessage());
            e.printStackTrace();
        }
        return i;
    }
    
    /**
     * 用户修改自己的书籍
     * @param bsBooks
     * @return
     * @throws ParseException
     */
    @RequestMapping("userUpdateBook")
    @ResponseBody
    public Integer userUpdateBook(@RequestBody BsBooks bsBooks, HttpSession session) throws ParseException {
        System.out.println("用户修改书籍数据: " + bsBooks);
        
        // 从session获取用户信息
        BsUsers currentUser = (BsUsers) session.getAttribute("user");
        if (currentUser == null) {
            System.out.println("用户未登录");
            return 0;
        }
        
        // 验证书籍是否属于当前用户
        BsBooks existingBook = this.bsBooksService.queryById(bsBooks.getBsBookid());
        if (existingBook == null || !existingBook.getBsUploaderid().equals(currentUser.getBsUserid())) {
            System.out.println("书籍不存在或不属于当前用户");
            return 0;
        }
        
        // 处理出版日期，如果为空则使用当前日期
        if (bsBooks.getBsBookbt1() != null && !bsBooks.getBsBookbt1().trim().isEmpty()) {
            System.out.println("处理出版日期: " + bsBooks.getBsBookbt1());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse(bsBooks.getBsBookbt1());
            java.sql.Date resultDate = new java.sql.Date(date.getTime());
            bsBooks.setBsBookbt(resultDate);
        } else {
            // 如果出版日期为空，使用当前日期
            System.out.println("出版日期为空，使用当前日期");
            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
            bsBooks.setBsBookbt(currentDate);
        }
        
        // 保持上传者信息不变
        bsBooks.setBsUploaderid(existingBook.getBsUploaderid());
        bsBooks.setBsUploadername(existingBook.getBsUploadername());
        
        System.out.println("准备更新数据库的数据: " + bsBooks);
        
        Integer i = 0;
        try {
            BsBooks updatedBook = this.bsBooksService.update(bsBooks);
            if (updatedBook != null) {
                i = 1;
            }
            System.out.println("用户修改书籍结果: " + i);
        } catch (Exception e) {
            System.out.println("用户修改书籍失败，异常信息: " + e.getMessage());
            e.printStackTrace();
        }
        return i;
    }
    
    /**
     * 用户删除自己的书籍
     * @param bsBooks
     * @return
     */
    @RequestMapping("userDeleteBook")
    @ResponseBody
    public Integer userDeleteBook(@RequestBody BsBooks bsBooks, HttpSession session) {
        System.out.println("用户删除书籍ID: " + bsBooks.getBsBookid());
        
        // 从session获取用户信息
        BsUsers currentUser = (BsUsers) session.getAttribute("user");
        if (currentUser == null) {
            System.out.println("用户未登录");
            return 0;
        }
        
        // 验证书籍是否属于当前用户
        BsBooks existingBook = this.bsBooksService.queryById(bsBooks.getBsBookid());
        if (existingBook == null || !existingBook.getBsUploaderid().equals(currentUser.getBsUserid())) {
            System.out.println("书籍不存在或不属于当前用户");
            return 0;
        }
        
        Integer i = 0;
        try {
            boolean result = this.bsBooksService.deleteById(bsBooks.getBsBookid());
            if (result) {
                i = 1;
            }
            System.out.println("用户删除书籍结果: " + i);
        } catch (Exception e) {
            System.out.println("用户删除书籍失败，异常信息: " + e.getMessage());
            e.printStackTrace();
        }
        return i;
    }
    
    /**
     * 查询用户上传的书籍
     * @param model
     * @param session
     * @return
     */
    @RequestMapping("userBooks")
    public String userBooks(Model model, HttpSession session) {
        // 从session获取用户信息
        BsUsers currentUser = (BsUsers) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:../index.jsp";
        }
        
        // 查询用户上传的书籍
        BsBooks queryBook = new BsBooks();
        queryBook.setBsUploaderid(currentUser.getBsUserid());
        List<BsBooks> userBooksList = this.bsBooksService.queryAll(queryBook);
        
        // 查询对应的出版社信息
        List<BsPress> bsPressList = new ArrayList<BsPress>();
        BsPress bsPress = new BsPress();
        for (BsBooks b : userBooksList) {
            bsPress.setBsPressnum(b.getBsPressnum());
            List<BsPress> bsPressList1 = this.bsPressService.queryAll(bsPress);
            if (bsPressList1 != null && !bsPressList1.isEmpty()) {
                bsPressList.add(bsPressList1.get(0));
            } else {
                BsPress defaultPress = new BsPress();
                defaultPress.setBsPressname("未知出版社");
                defaultPress.setBsPressnum(b.getBsPressnum());
                bsPressList.add(defaultPress);
            }
        }
        
        model.addAttribute("userBooksList", userBooksList);
        model.addAttribute("bsPressList", bsPressList);
        model.addAttribute("currentUser", currentUser);
        return "userBooks";
    }
}