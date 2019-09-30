package com.cui.controller;

import com.cui.pojo.User;
import com.cui.service.UserService;
import com.cui.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping(value = "/user")
@Api(value = "用户操作接口", tags = "用户的登录注册等")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    ImageCut imageCut;

    @RequestMapping(value = "/validateEmail", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "验证邮箱", notes = "验证邮箱")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", value = "邮箱", required = true, dataType = "string")
    })
    public String validateEmail(String email) {
        int i = userService.validateEmail(email);
        return i > 0 ? "fail" : "success";
    }

    @RequestMapping(value = "/insertUser", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "注册用户", notes = "注册新用户")
    public String insertUser(User user, HttpSession session) {
        int i = userService.insertUser(user);
        if (i > 0) {
            session.setAttribute("userAccount", user.getEmail());
            return "success";
        }
        return "fail";
    }

    @RequestMapping(value = "/loginUser", method = RequestMethod.POST)
    @ResponseBody()
    @ApiOperation(value = "登录验证", notes = "登录")
    public String loginUser(User user, HttpSession session) {
        int i = userService.loginUser(user);
        if (i > 0) {
            session.setAttribute("userAccount", user.getEmail());
            return "success";
        }
        return "fail";
    }

    @RequestMapping("/showMyProfile")
    public String showMyProfile(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userAccount");
        User user = userService.selectUserByEmail(email);
        user.setImgurl(InfoUtils.getProperties("IMG_URL") + user.getImgurl());
        model.addAttribute("user", user);
        return "/before/my_profile";
    }

    @RequestMapping("/changeProfile")
    public String changeProfile(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userAccount");
        User user = userService.selectUserByEmail(email);
        user.setImgurl(InfoUtils.getProperties("IMG_URL") + user.getImgurl());
        model.addAttribute("user", user);
        return "/before/change_profile";
    }

    @RequestMapping("/changeAvatar")
    public String changeAvatar(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userAccount");
        User user = userService.selectUserByEmail(email);
        user.setImgurl(InfoUtils.getProperties("IMG_URL") + user.getImgurl());
        model.addAttribute("user", user);
        return "/before/change_avatar";
    }

    @RequestMapping("/updateUser")
    public String updateUser(User user) {
        int i = userService.updateUser(user);
        return "redirect:/user/showMyProfile";
    }

    @RequestMapping("/upLoadImage")
    public String upLoadImage(String email, MultipartFile image_file, String x1, String x2, String y1, String y2, HttpSession session) {
        String oldFilename = image_file.getOriginalFilename();
        String suffix = oldFilename.substring(oldFilename.lastIndexOf("."));
        String newFileName = UUIDUtils.getUUID() + suffix;
        String img_path = InfoUtils.getProperties("IMG_PATH");
        File file = new File(img_path, newFileName);
        try {
            image_file.transferTo(file);
        } catch (IOException e) {

        }
        float _x1 = Float.valueOf(x1);
        float _x2 = Float.valueOf(x2);
        float _y1 = Float.valueOf(y1);
        float _y2 = Float.valueOf(y2);
        imageCut.cutImage(img_path + "/" + newFileName, (int) _x1, (int) _y1, (int) (_x2 - _x1), (int) (_y2 - _y1));
        User user = new User();
        user.setImgurl(newFileName);
        user.setEmail(email);
        userService.updateUser(user);
        return "redirect:/user/showMyProfile";

    }

    @RequestMapping("/passwordSafe")
    public String passwordSafe(HttpSession session, Model model) {
        String email = (String) session.getAttribute("userAccount");
        User user = userService.selectUserByEmail(email);
        model.addAttribute("user", user);
        return "/before/password_safe";
    }

    @RequestMapping("/validatePassword")
    @ResponseBody
    public String validatePassword(String password,HttpSession session) {
        String email = (String) session.getAttribute("userAccount");
        User user = userService.selectUserByEmail(email);
        if (user.getPassword().equals(password)) {
            return "success";
        }
        return "fail";
    }
    @RequestMapping("/updatePassword")
    public String updatePassword(User user) {
        int i = userService.updateUser(user);
        return "redirect:/user/showMyProfile";
    }

    @RequestMapping("/loginOut2")
    public String loginOut2(HttpSession session){
        session.removeAttribute("userAccount");
        return "redirect:/index.jsp";
    }
    @RequestMapping("/forgetPassword")
    public String forgetPassword(){
        return "/before/forget_password";
    }
    @RequestMapping("/validateEmailCode")
    @ResponseBody
    public String validateEmailCode(HttpSession session,String code){
        String code1 = (String) session.getAttribute("code");
        if(code1.equals(code)) {
            return "success";
        }
        return "fail";
    }
    @RequestMapping("/sendEmail")
    @ResponseBody
    public String sendEmail(HttpSession session,String email){
        String code = Code.code();
        MailUtils.sendMail(email,"您的验证码是:"+ code,"找回密码");
        session.setAttribute("code",code);
        return "success";

    }
    @RequestMapping("/reset")
    public String reset(String email,Model model) {
        model.addAttribute("email",email);
        return "/before/reset_password";
    }
    @RequestMapping("/resetPassword")
    public String resetPassword(String password02,String email) {
        User user =new User();
        user.setPassword(password02);
        user.setEmail(email);
        int i = userService.updateUser(user);
        return "redirect:/index.jsp";
    }



}
