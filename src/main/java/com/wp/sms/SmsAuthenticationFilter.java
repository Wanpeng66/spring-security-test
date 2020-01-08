package com.wp.sms;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: wp
 * @Title: SmsAuthenticationFilter
 * @Description: TODO
 * @date 2020/1/8 21:44
 */
public class SmsAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    public static final String SPRING_SECURITY_FORM_PHONE = "phone";
    public static final String SPRING_SECURITY_FORM_CODE= "code";
    private String phoneParameter = "phone";
    private String codeParameter = "code";
    private boolean postOnly = true;

    public SmsAuthenticationFilter(){
        super(new AntPathRequestMatcher("/sms/login", "POST"));
    }

    protected SmsAuthenticationFilter( String defaultFilterProcessesUrl ) {
        super( defaultFilterProcessesUrl );
    }

    protected SmsAuthenticationFilter( RequestMatcher requiresAuthenticationRequestMatcher ) {
        super( requiresAuthenticationRequestMatcher );
    }

    @Override
    public Authentication attemptAuthentication( HttpServletRequest request, HttpServletResponse response ) throws AuthenticationException, IOException, ServletException {
        if (this.postOnly && ! request.getMethod().equals( "POST" )) {
            throw new AuthenticationServiceException( "Authentication method not supported: " + request.getMethod() );
        } else {
            String Phone = this.obtainPhone( request );
            String Code = this.obtainCode( request );
            if (Phone == null) {
                Phone = "";
            }

            if (Code == null) {
                Code = "";
            }

            Phone = Phone.trim();
            SmsAuthenticationToken authRequest = new SmsAuthenticationToken( Phone, Code );
            this.setDetails( request, authRequest );
            return this.getAuthenticationManager().authenticate( authRequest );
        }
    }

    @Nullable
    protected String obtainPhone(HttpServletRequest request) {
        return request.getParameter(this.phoneParameter);
    }

    @Nullable
    protected String obtainCode(HttpServletRequest request) {
        return request.getParameter(this.codeParameter);
    }

    protected void setDetails(HttpServletRequest request, SmsAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public void setPhoneParameter( String phoneParameter ) {
        this.phoneParameter = phoneParameter;
    }

    public void setCodeParameter( String codeParameter ) {
        this.codeParameter = codeParameter;
    }

    public void setPostOnly( boolean postOnly ) {
        this.postOnly = postOnly;
    }

    public String getPhoneParameter() {
        return phoneParameter;
    }

    public String getCodeParameter() {
        return codeParameter;
    }
}
