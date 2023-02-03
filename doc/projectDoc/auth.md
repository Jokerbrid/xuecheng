# 系统登录模块：

集成SpringSecurity和Oauth2进行开发：

```xml
引入springSecurity的依赖：
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-oauth2</artifactId>
        </dependency>
```

## 配置Security:

理解整个springSecurity框架的验证过程：

配置WebSecurityConfig配置类，继承WebSecurityConfigAdapter类。

```java
    //配置认证管理：
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }   
	//配置安全拦截：
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/r/**").permitAll()//访问/r/**的资源放行
                .anyRequest().authenticated()//其它请求均需认证
                .and()
                .formLogin().successForwardUrl("/login-success")
                .and().logout().logoutUrl("/logout");//登录成功跳转到/login-success
    }
    //配置编码：
    @Bean
    public PasswordEncoder passwordEncoder() {
        //密文：
        return new BCryptPasswordEncoder();
    }
```

由于SpringSecurity框架在进行账号密码校验的时候：由AuthenticationManager认证管理器调用UserDetailsService的loadUserByname方法进行验证。

实现UserDetailsService 重写该方法，将认证路径指向数据库，返回UserDetails。

![](..\img\auth-spring.png)

```java
//需要继承DaoAuthenticationProvider:
	    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        //不进行Spring-security框架的密码对比：
        //super.additionalAuthenticationChecks(userDetails, authentication);
    }
//屏蔽框架的自动密码比对；由loadUserByName方法来比对。
```

由于认证方式的多样化：

​	可以选择手机发送验证码获取登录token，使用账号密码获得登录token。以及第三方授权注册并登录。





配置token配置类,使用JWT增强器，生成JWT类型的token:

```java
	private String SIGNING_KEY="mq123";//签名密钥；  
	@Bean
    public TokenStore tokenStore(){
        return new InMemoryTokenStore();//使用内存存储令牌.
    }    
	//token服务
    @Bean(name = "AuthorizationServerTokenServicesCustom")
    public AuthorizationServerTokenServices tokenServices(){
        DefaultTokenServices services=new DefaultTokenServices();
        services.setTokenStore(tokenStore());//注入令牌存储策略
        services.setSupportRefreshToken(true);//支持刷新令牌


        //配置令牌增强器：(生成JWT)
        TokenEnhancerChain tokenEnhancerChain=new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter()));

        services.setTokenEnhancer(tokenEnhancerChain);//设置token链。


        services.setAccessTokenValiditySeconds(60*60*2);//设置令牌默认有效时间 2小时
        services.setRefreshTokenValiditySeconds(3*24*60*60);//刷新令牌默认有效时间 3天
        return services;
    }
    //注册转换器：
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter converter=new JwtAccessTokenConverter();
        converter.setSigningKey(SIGNING_KEY);
        return converter;
    }

```

## 配置oauth2服务端：

```java
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {
    @Resource(name="AuthorizationServerTokenServicesCustom")
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;


    //配置客户端详细服务：
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .inMemory()//使用inMemory存储
                .withClient("xcWebApp")  									//client_id
                //.secret("xcWebApp")										//client_secret
                .secret( passwordEncoder.encode("xcWebApp")) //客户端密钥
                .resourceIds("res_content_1","res_learing_1")//资源列表
                .authorizedGrantTypes("authorization_code", "password","client_credentials","implicit","refresh_token")
                // 该client允许的授权类型authorization_code,password,refresh_token,implicit,client_credentials
                .scopes("all") //允许的授权范围
                .autoApprove(false)//不自动跳转到授权页面
                .redirectUris("https://www.baidu.com","http://localhost:6060/login-success");//授权后跳转到。

    }

    //配置令牌端点的安全配置：
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")           //
                .allowFormAuthenticationForClients();//表单认证(申请令牌)
    }
    //令牌端点的访问配置：
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)//认证管理器
                .tokenServices(authorizationServerTokenServices)//令牌管理服务
                .allowedTokenEndpointRequestMethods(HttpMethod.GET)
                ;//允许请求的方法。
    }
}
```

配置完毕后，就可以认证了：

```
通过账号密码进行登录获取token;
POST http://ip:port/oauth/token?
param:
client_id= &
client_secret= &
grant_type= &
username= &
password= 
返回：
{
  "access_token": "",
  "token_type": "bearer",
  "refresh_token": "",
  "expires_in": num,
  "scope": "all",
  "jti": ""
}

验证token值:
POST http://ip:port/oauth/check_token?
token=
返回：
{
token的解析值
}
```

## 改造token:

了解完token机制以及SpringSecurity的认证机制，开始编程。由于loadUserByUsername最后需要返回封装类UserDetails,并且Username是String类型，因此可以将username转换成json串传递。

```java
        //token中不存放密码保证数据安全:
        user.setPassword(null);
        //将user转化为json:
        String userString=gson.toJson(user);
        //将授权信息封装成字符串数组：
        String[] authorities=permissions.toArray(new String[0]);
        //创建UserDetails对象：
        UserDetails userDetails = User.withUsername(userString).password(password).authorities(authorities).build();
```

这样token里就携带了很多的user信息。



## 封装SecurityUtil：

创建一个工具类来获取user的信息类，即将原来token中的username json串转成为user对象。

```java

Object principalObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();//拿到username 的json串
```

封装完毕后，获取用户信息只需要调用 getUser()方法就能拿到用户登录的全部信息了。