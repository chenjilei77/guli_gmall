package com.chen.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.chen.gmall.bean.UmsMember;
import com.chen.gmall.service.UserService;
import com.chen.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    private UserService userService;

    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember,String currentIp){
        String token="";
        UmsMember umsMemberLogin = userService.login(umsMember);

        if(umsMemberLogin!=null){
            //登陆成功


            //用Jwt制作token
            String userId = umsMemberLogin.getId();
            String nickname = umsMemberLogin.getNickname();
            Map<String,Object> userMap = new HashMap<>();

            userMap.put("memberId",userId);
            userMap.put("nickName",nickname);

            token = JwtUtil.encode("2020gmall07", userMap, currentIp);
            //将token放入redis一份

            userService.addUserToken(token,userId);
        }else{
            token="fail";
        }

        return token;
    }


    @RequestMapping("index")
    public String index(String returnUrl, ModelMap modeMap){

        modeMap.put("returnUrl",returnUrl);
        return "index";
    }
    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token,HttpServletRequest request){
        //通过jwt校验token真假
        Map<String,String> map  = new HashMap<>();

        String ip = request.getHeader("x-forwarded-for");
        if(StringUtils.isBlank(ip)){
            ip = request.getRemoteAddr();//从request中获取IP
        }
        if(StringUtils.isBlank(ip)){
            ip="127.0.0.1";
        }
        Map<String, Object> decode = JwtUtil.decode(token, "2020gmall07", ip);

        if(decode!=null){
            map.put("status","success");
            map.put("memberId",(String)decode.get("memberId"));
            map.put("nickName",(String)decode.get("nickName"));
        }else{
            map.put("status","fail");
        }



        return JSON.toJSONString(map);
    }
}
