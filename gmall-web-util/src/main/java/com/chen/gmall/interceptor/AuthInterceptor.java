package com.chen.gmall.interceptor;

import com.alibaba.fastjson.JSON;
import com.chen.gmall.annotations.LoginRequired;
import com.chen.gmall.util.CookieUtil;
import com.chen.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter{


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String newToken = request.getParameter("newToken");
//        if(newToken!=null&&newToken.length()>0){
//            CookieUtil.setCookie(request,response,"token",newToken,WebConst.cookieExpire,false);
//        }
        //拦截代码
        HandlerMethod handler1 = (HandlerMethod)handler;

        LoginRequired methodAnnotation = handler1.getMethodAnnotation(LoginRequired.class);
        if (methodAnnotation==null){
            return true;
        }
        String token = "";
        String oldToken= CookieUtil.getCookieValue(request,"oldToken",true);
        if(StringUtils.isNotBlank(oldToken)){
            token=oldToken;
        }
        String newToken = request.getParameter("token");
        if(StringUtils.isNotBlank(newToken)){
            token=newToken;
        }
        boolean loginSuccess = methodAnnotation.loginSuccess();

        //调用验证中心验证
        String success = "fail";
        Map<String,String> successMap = new HashMap<>();
        if(StringUtils.isNotBlank(token)){

            String ip = request.getHeader("x-forwarded-for");
            if(StringUtils.isBlank(ip)){
                ip = request.getRemoteAddr();//从request中获取IP
            }
            if(StringUtils.isBlank(ip)){
                ip="127.0.0.1";
            }
            String successJson = HttpclientUtil.doGet("http://127.0.0.1:8085/verify?"+token+"&currentIp="+ip);
            successMap = JSON.parseObject(successJson, Map.class);
            success = successMap.get("status");
        }

        if(loginSuccess){
            //必须登陆成功才能使用
            if(!success.equals("success")){
                //重定向回passport登陆
                StringBuffer requestURL = request.getRequestURL();
                response.sendRedirect("http://127.0.0.1:8085/index?ReturnUrl="+requestURL.toString());
                return false;
            }
                //验证通过，覆盖cookie中的token
                request.setAttribute("memberId",successMap.get("memberId"));
                request.setAttribute("nickName",successMap.get("nickName"));
        }else{
            //没有登陆也可以使用，但是需要验证
            //验证token
            if(success.equals("success")){
                //需要将token携带的用户信息写入cookie
                request.setAttribute("memberId",successMap.get("memberId"));
                request.setAttribute("nickName",successMap.get("nickName"));
            }
        }
        if(StringUtils.isNotBlank(token))
             CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);

        return true;
    }
}
