package com.wp.sms;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: wp
 * @Title: SmsAuthenticationProvider
 * @Description: TODO
 * @date 2020/1/8 21:54
 */
public class SmsAuthenticationProvider implements AuthenticationProvider {
    private UserDetailsService smsUserDetailsService;
    @Override
    public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
        SmsAuthenticationToken token = (SmsAuthenticationToken)authentication;
        Object phone = token.getPrincipal();
        Object code = token.getCredentials();
        check(phone,code);
        //拿到用户信息，返回给filter
        UserDetails userDetails = this.getSmsUserDetailsService().loadUserByUsername( (String) phone );
        if (userDetails == null) {
            throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
        }
        SmsAuthenticationToken targetToken = new SmsAuthenticationToken( userDetails.getAuthorities(),userDetails.getUsername(),userDetails.getPassword() );
        targetToken.setDetails( token.getDetails() );
        return targetToken;
    }

    private void check( Object phone, Object code ) throws AuthenticationException {
        HttpServletRequest request = ( (ServletRequestAttributes) RequestContextHolder.getRequestAttributes() ).getRequest();
        String targetCode = (String) request.getSession().getAttribute( SmsAuthenticationFilter.SPRING_SECURITY_FORM_CODE );
        String targetPhone = (String) request.getSession().getAttribute( SmsAuthenticationFilter.SPRING_SECURITY_FORM_PHONE );
        if(StringUtils.isEmpty( targetCode )){
            throw new BadCredentialsException( "未检测到申请验证码" );
        }
        if(!phone.toString().equals( targetPhone )){
            throw new BadCredentialsException("申请的手机号码与登录手机号码不一致");
        }

        if(!code.toString().equals( targetCode )){
            throw new BadCredentialsException("验证码错误");
        }


    }

    @Override
    public boolean supports( Class<?> aClass ) {
        return SmsAuthenticationToken.class.isAssignableFrom( aClass );
    }

    public UserDetailsService getSmsUserDetailsService() {
        return smsUserDetailsService;
    }

    public void setSmsUserDetailsService( UserDetailsService smsUserDetailsService ) {
        this.smsUserDetailsService = smsUserDetailsService;
    }
}
