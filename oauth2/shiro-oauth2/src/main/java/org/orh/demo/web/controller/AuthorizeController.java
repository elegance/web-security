package org.orh.demo.web.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.orh.demo.Constant;
import org.orh.demo.domain.Client;
import org.orh.demo.repository.ClientRepository;
import org.orh.demo.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizeController {
    @Autowired
    private ClientRepository clientRepository;
    
//    @Autowired
//    private UserRepository userRepository;
    
    @Autowired
    private OAuthService oAuthService;
    
    /**
     * 向用户申请授权 
     */
    @RequestMapping("/authorize")
    public Object authorize(Model model, HttpServletRequest request) throws URISyntaxException, OAuthSystemException {
        // 构建OAuth 授权请求
        try {
            OAuthAuthzRequest oauthRequest= new OAuthAuthzRequest(request);
            
            Client client = clientRepository.findByClientId(oauthRequest.getClientId());
            if (client == null) { // 检查传入的客户端ID是否正确
                OAuthResponse response = OAuthASResponse
                        .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                        .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                        .setErrorDescription(Constant.INVALID_CLIENT_DESCRIPTION)
                        .buildJSONMessage();
                return new ResponseEntity<String>(response.getBody(), HttpStatus.valueOf(response.getResponseStatus()));
            }
            
            Subject subject = SecurityUtils.getSubject();
            // 如果用户没有登录，跳转到登录页
            if (!subject.isAuthenticated()) {
                if (!login(subject, request)) { // 登录失败是跳转登录页面
                    model.addAttribute("client", client.getClientId());
                    return "oauth2login";
                }
            }
            
            String username = (String) subject.getPrincipal();
            
            // 生成授权码
            String authorizationCode = null;
            
            // responseType 目前只支持CODE，另外还有TOKEN
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
            
            if (responseType.equals(ResponseType.CODE.toString())) {
                OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                authorizationCode = oauthIssuerImpl.authorizationCode();
                oAuthService.addAuthCode(authorizationCode, username);
            }
            
            // 进行OAuth响应构建
            OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND); // 302 重定向
            builder.setCode(authorizationCode); // 设置授权码
            String redirectURI = oauthRequest.getRedirectURI(); // 得到重定向地址

            // 构建响应
            final OAuthResponse response = builder.location(redirectURI).buildQueryMessage();
            
            // 302 重定向需要设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(redirectURI));
            return new ResponseEntity<Object>(headers, HttpStatus.valueOf(response.getResponseStatus()));
        }  catch (OAuthProblemException e) {
            // 出错处理
            String redirectUri = e.getRedirectUri();
            
            if (OAuthUtils.isEmpty(redirectUri)) {
                return new ResponseEntity<String>("OAuth callback url needs to be provided by client!!", HttpStatus.NOT_FOUND);
            }
            
           // 返回其他错误
            final OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                    .error(e).location(redirectUri).buildQueryMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI(response.getLocationUri()));
            return new ResponseEntity<Object>(headers, HttpStatus.valueOf(response.getResponseStatus()));
        }
    }

    private boolean login(Subject subject, HttpServletRequest request) {
        if ("get".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return false;
        }

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        try {
            subject.login(token);
            return true;
        } catch (Exception e) {
            request.setAttribute("error", "登录失败：" + e.getClass().getName());
            return false;
        }
    }
}
