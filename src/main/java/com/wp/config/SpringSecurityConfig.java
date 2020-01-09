package com.wp.config;

import com.wp.security.CustomAccessDeniedHandler;
import com.wp.security.CustomExpiredSessionStrategy;
import com.wp.security.VerifyFilter;
import com.wp.service.impl.UserDetailService;
import com.wp.sms.SmsAuthenticationSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

import javax.sql.DataSource;

/**
 * @author: wp
 * @Title: SpringSecurityConfig
 * @Description: TODO
 * @date 2020/1/3 10:57
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserDetailService customUserDetailService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    PermissionEvaluator customPermissionEvaluator;
    @Autowired
    CustomAccessDeniedHandler customAccessDeniedHandler;
    @Autowired
    CustomExpiredSessionStrategy customExpiredSessionStrategy;
    @Autowired
    private FindByIndexNameSessionRepository mySessionRepository;
    @Autowired
    SmsAuthenticationSecurityConfig smsAuthenticationSecurityConfig;

    // 是session为Spring Security提供的
    // 用于在集群环境下控制会话并发的会话注册表实现
    @Bean
    public SpringSessionBackedSessionRegistry springSessionBackedSessionRegistry(){

        return new SpringSessionBackedSessionRegistry(mySessionRepository);
    }

    @Bean
    public RoleHierarchy roleHierarchy(){
        RoleHierarchyImpl  roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy( "ROLE_ADMIN > ROLE_USER" );
        return roleHierarchy;
    }

    @Override
    public void configure( HttpSecurity http) throws Exception {
        http.apply( smsAuthenticationSecurityConfig );
        http.authorizeRequests()
                // 如果有允许匿名的url，填在下面
//                .antMatchers().permitAll()
                .antMatchers("/captcha.jpg","/invaild/session","/sms/**" ).permitAll()
                .anyRequest().authenticated()
                .and()//.addFilterBefore( new VerifyFilter(), UsernamePasswordAuthenticationFilter.class )
                // 设置登陆页
                .formLogin().loginPage("/login")
                // 设置登陆成功页,失败页面
                .defaultSuccessUrl("/").failureForwardUrl( "/login/error" ).permitAll()
                // 自定义登陆用户名和密码参数，默认为username和password
//                .usernameParameter("username")
//                .passwordParameter("password")
                .and().logout().deleteCookies("SESSIONID").permitAll()
                .and().rememberMe().tokenRepository( persistentTokenRepository() )
                //设置权限不足的返回逻辑
                .and().exceptionHandling()/*.accessDeniedHandler( customAccessDeniedHandler )*/.accessDeniedPage( "/403" )
                .and().sessionManagement().invalidSessionUrl( "/invaild/session" )
                .maximumSessions( 1 ).maxSessionsPreventsLogin( false )
                .expiredSessionStrategy( customExpiredSessionStrategy )
                .sessionRegistry( springSessionBackedSessionRegistry() );

        // 关闭CSRF跨域
        http.csrf().disable();

    }

    //解决不抛出UsernameNotFoundException的问题
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        //此参数为true时，会对UsernameNotFoundException进行包装，变成BadCredentialsException
        provider.setHideUserNotFoundExceptions(false);
        provider.setUserDetailsService(customUserDetailService);
        provider.setPasswordEncoder(new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return charSequence.toString();
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                return s.equals(charSequence.toString());
            }
        } );
        return provider;
    }

    /*@Override
    protected void configure( AuthenticationManagerBuilder auth) throws Exception{
        //基于内存来存储用户信息
        *//*auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .withUser("user").password(new BCryptPasswordEncoder().encode("123")).roles("USER").and()
                .withUser("admin").password(new BCryptPasswordEncoder().encode("456")).roles("USER","ADMIN");*//*
        auth.userDetailsService( customUserDetailService ).passwordEncoder( new PasswordEncoder() {
            @Override
            public String encode(CharSequence charSequence) {
                return charSequence.toString();
            }

            @Override
            public boolean matches(CharSequence charSequence, String s) {
                return s.equals(charSequence.toString());
            }
        } );

    }*/

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置拦截忽略文件夹，可以对静态资源放行
        web.ignoring().antMatchers("/css/**", "/js/**");
        //springsecurity默认是通过role去作访问控制，如果需要更细粒度的根据权限访问控制，则需要提供权限解释器
        DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        defaultWebSecurityExpressionHandler.setPermissionEvaluator(customPermissionEvaluator);
        web.expressionHandler(defaultWebSecurityExpressionHandler );
    }


    @Bean
    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // 如果token表不存在，使用下面语句可以初始化该表；若存在，请注释掉这条语句，否则会报错。
//        tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }

    /*@Bean
    public HttpSessionEventPublisher httpSessionEventPublisher (){
        return new HttpSessionEventPublisher();
    }*/


}
