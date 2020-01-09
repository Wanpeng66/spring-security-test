package com.wp.web;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.userdetails.User;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author: wp
 * @Title: LoginController
 * @Description: TODO
 * @date 2020/1/6 15:53
 */
@Controller
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    DefaultKaptcha defaultKaptcha;
    @Autowired
    SpringSessionBackedSessionRegistry springSessionBackedSessionRegistry;


    @RequestMapping("/")
    public String showHome() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        return "home";
    }
    @RequestMapping("/403")
    public String showIndex() {
        return "403";
    }

    @RequestMapping("/invaild/session")
    @ResponseBody
    public String sessionTimeOut() {
        return "无效的session......";
    }


    @RequestMapping("/sms/code")
    @ResponseBody
    public void sms(String mobile, HttpSession session) {
        int code = (int) Math.ceil(Math.random() * 9000 + 1000);
        session.setAttribute("phone", mobile);
        session.setAttribute("code", code+"");
        logger.info("{}：为 {} 设置短信验证码：{}", session.getId(), mobile, code);
    }

    @RequestMapping("/login")
    public String showLogin() {
        return "login";
    }

    @RequestMapping("/login/error")
    public void loginError( HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        AuthenticationException exception =
                (AuthenticationException)request.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        if(null==exception){
            exception = (AuthenticationException)request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        }
        try {
            response.getWriter().write(exception.toString());
        }catch (IOException e) {
            logger.error( "登录失败,e:",e );
        }
    }

    @RequestMapping("/admin")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String printAdmin() {
        return "如果你看见这句话，说明你有ROLE_ADMIN角色";
    }

    @RequestMapping("/user")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_USER')")
    public String printUser() {
        return "如果你看见这句话，说明你有ROLE_USER角色";
    }

    @RequestMapping("/admin/r")
    @ResponseBody
    @PreAuthorize("hasPermission('/admin/r','r')")
    public String printAdminR() {

        return "如果你看见这句话，说明你访问/admin/r路径具有r权限";
    }

    @RequestMapping("/admin/c")
    @ResponseBody
    @PreAuthorize("hasPermission('/admin/c','c')")
    public String printAdminC() {
        return "如果你看见这句话，说明你访问/admin/c路径具有c权限";
    }

    @RequestMapping("/captcha.jpg")
    public void applyCheckCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        byte[] verByte = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            //生产验证码字符串并保存到session中
         String createText = defaultKaptcha.createText();
            request.getSession().setAttribute("verify_session_Code", createText);
            //使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
         BufferedImage challenge = defaultKaptcha.createImage(createText);
            ImageIO.write(challenge, "jpg", jpegOutputStream);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
        verByte = jpegOutputStream.toByteArray();
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(verByte);
        responseOutputStream.flush();
        responseOutputStream.close();

    }

    @GetMapping("/kick")
    @PreAuthorize( "hasRole('ROLE_ADMIN')" )
    @ResponseBody
    public String removeUserSessionByUsername(@RequestParam String username) {
        int count = 0;
        List<SessionInformation> sessionsInfo = springSessionBackedSessionRegistry.getAllSessions(username, false);
        if (null != sessionsInfo && sessionsInfo.size() > 0) {
            for (SessionInformation sessionInformation : sessionsInfo) {
                sessionInformation.expireNow();
                count++;
            }
        }
        return "操作成功，清理session共" + count + "个";
    }


    /*@GetMapping
    @ResponseBody
    public String getAllSessions(){

    }*/


}
