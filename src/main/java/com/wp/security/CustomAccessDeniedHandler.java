package com.wp.security;

import com.alibaba.fastjson.JSONObject;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: wp
 * @Title: CustomAccessDeniedHandler
 * @Description: TODO
 * @date 2020/1/8 11:08
 */
@Component
public class CustomAccessDeniedHandler extends AccessDeniedHandlerImpl {
    @Override
    public void handle( HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException ) throws IOException, ServletException {
        //判断是否为ajax请求
        if (request.getHeader("accept").indexOf("application/json") > -1
                || (request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").equals(
                "XMLHttpRequest"))) {
            //设置状态为403，无权限状态
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            //设置格式以及返回json数据 方便前台使用reponseJSON接取
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            PrintWriter out = response.getWriter();
            JSONObject json = new JSONObject();
            json.put("message","权限不足，请联系管理员");
            out.append(json.toString());
        }else{
            super.handle(request,response,accessDeniedException);
        }

    }
}
