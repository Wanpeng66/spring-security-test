package com.wp.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wp
 * @Title: CustomExpiredSessionStrategy
 * @Description: TODO
 * @date 2020/1/8 13:44
 */
@Component
public class CustomExpiredSessionStrategy implements SessionInformationExpiredStrategy {
    private ObjectMapper objectMapper = new ObjectMapper();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Override
    public void onExpiredSessionDetected( SessionInformationExpiredEvent event ) throws IOException, ServletException {
        Map<String, Object> map = new HashMap<>(16);
        map.put("code", 0);
        map.put("msg", "您的会话已过期，请重新登陆。" + event.getSessionInformation().getLastRequest());
        // Map -> Json
        String json = objectMapper.writeValueAsString(map);
        /*Cookie session = new Cookie( "SESSIONID","" );
        session.setMaxAge( 0 );
        event.getResponse().addCookie( session );
        Cookie remember = new Cookie( "remember-me","" );
        session.setMaxAge( 0 );
        event.getResponse().addCookie( remember );*/
        event.getResponse().setContentType("application/json;charset=UTF-8");
        event.getResponse().getWriter().write(json);

        // 如果是跳转html页面，url代表跳转的地址
        // redirectStrategy.sendRedirect(event.getRequest(), event.getResponse(), "url");

    }
}
