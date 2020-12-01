package org.javatoy.provider;

import org.javatoy.commons.UserTest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class RegisterController {


    //  post 的响应接口 一定要是 302   ，不然无效
    //  重定向的地址一定要 是 绝对路径，不能写相对路径
    @PostMapping("/register")
    public String register(UserTest userTest){
        return "redirect:Http://provider/loginPage?username="+userTest.getUsername();
    }


    @GetMapping("/loginPage")
    @ResponseBody
    public String loginPage(String username){
        return "loginPage:"+username;
    }
}
