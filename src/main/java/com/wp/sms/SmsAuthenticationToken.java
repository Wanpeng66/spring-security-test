package com.wp.sms;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author: wp
 * @Title: SmsAuthenticationToken
 * @Description: TODO
 * @date 2020/1/8 21:35
 */
public class SmsAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 520L;
    private final Object phone;
    private Object code;


    public SmsAuthenticationToken( Collection<? extends GrantedAuthority> authorities, Object phone, Object code ) {
        super( authorities );
        this.phone = phone;
        this.code = code;
        super.setAuthenticated(true);
    }

    public SmsAuthenticationToken( Object phone, Object code ) {
        super( null );
        this.phone = phone;
        this.code = code;
        this.setAuthenticated( false );
    }

    @Override
    public Object getCredentials() {
        return this.code;
    }

    @Override
    public Object getPrincipal() {
        return this.phone;
    }

    @Override
    public void setAuthenticated( boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        } else {
            super.setAuthenticated(false);
        }
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.code = null;
    }
}
