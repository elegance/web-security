package org.orh.demo.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.orh.demo.domain.User;
import org.orh.demo.repository.UserRepository;
import org.orh.demo.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuthService oauthService;

    @PostMapping(value = "/user")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        user = userRepository.save(user);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @GetMapping(value = "/user")
    public Iterable<User> findUser() {
        return userRepository.findAll();
    }

    @GetMapping(value = "/user/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return userRepository.findOne(id);
    }

    @RequestMapping("/userInfo")
    public HttpEntity<String> userInfo(HttpServletRequest request) throws OAuthSystemException {
        try {
            // 构建 OAuth 资源请求
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.QUERY);
            String accessToken = oauthRequest.getAccessToken();

            // 验证token
            if (!oauthService.checkAccessToken(accessToken)) {
                // 如果不存在/过期了，返回未验证错误，需要重新验证
                OAuthResponse response = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                        .setRealm("useInfo-resource-server").setError(OAuthError.ResourceResponse.INVALID_TOKEN).buildJSONMessage();

                HttpHeaders headers = new HttpHeaders();
                headers.add(OAuth.HeaderType.WWW_AUTHENTICATE, response.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
                response.getHeaders();
                return new ResponseEntity<String>(headers, HttpStatus.UNAUTHORIZED);
            }

            // 返回用户名
            String username = oauthService.getUsernameByAccessToken(accessToken);
            return new ResponseEntity<String>(username, HttpStatus.OK);

        } catch (OAuthProblemException e) {
            String errorCode = e.getError();
            if (OAuthUtils.isEmpty(errorCode)) {
                OAuthResponse oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                        .setRealm("userInfo-resource-server").buildHeaderMessage();
                HttpHeaders headers = new HttpHeaders();
                headers.add(OAuth.HeaderType.WWW_AUTHENTICATE, oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
                return new ResponseEntity<String>(headers, HttpStatus.UNAUTHORIZED);
            }
            OAuthResponse oauthResponse =
                    OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED).setRealm("userInfo-resource-server")
                            .setError(e.getError()).setErrorDescription(e.getDescription()).setErrorUri(e.getUri()).buildHeaderMessage();
            HttpHeaders headers = new HttpHeaders();
            headers.add(OAuth.HeaderType.WWW_AUTHENTICATE, oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE));
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
}
