package org.orh;

import javax.servlet.http.HttpServletRequest;

import org.orh.MockUtil.OAuth2Client;
import org.orh.MockUtil.User;
import org.orh.UserController.RetMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import lombok.AllArgsConstructor;

@RestController
@CrossOrigin
public class OAuth2Controller {

    @Autowired
    protected HttpServletRequest request;

    /**
     * 1. 向用户申请授权 - 供第三方/外部调用
     */
    @GetMapping(path = "/oauth2/authorize") // 一般此地址出现在 —— 三方应用的 view上，用户可见
    public ModelAndView authorize(@RequestParam String client_id, @RequestParam String redirect_uri, String state) {
        // * 如果用户还未进行认证，也就是服务器根本不知道是谁在请求，那么就需要先登录进行认证
        // * 如果用户已经通过了认证，那么就导向 授权界面
        // * optimization feature: 有时候，在短时间内，如果用户已经授权过该应用，就不再导向用户授权界面，直接发放授权码重定向URI

        Assert.isTrue(MockUtil.checkClient(client_id), "无效的client_id");

        ModelAndView mv = null;

        User user = (User) request.getSession().getAttribute(UserController.USER_SESSION_KEY); // 这里本身是耦合
                                                                                               // 本系统的用户体系的

        if (user == null) {
            // 先去 登录认证 界面
            mv = new ModelAndView("/identify/login.btl");
        } else {
            // 授权界面
            mv = new ModelAndView("/oauth2/authorize.btl");
        }
        OAuth2Client client = MockUtil.getClientInfo(client_id);
        mv.addObject("client_name", client.getClientName());
        mv.addObject("client_id", client_id);
        mv.addObject("redirect_uri", redirect_uri);
        mv.addObject("state", state);

        return mv;
    }

    /**
     * 2.1 用户同意授权-发放授权码 code，重定向 URI -- (用户是否同意的判断是完全在自己的服务中判断的，外部无权控制)
     * 这里的接口谁都有权来访问，岂不是可以跳过用户的授权，直接申请授权码？ 确保请求来自 "/oauth2/authorize"，先发一个 ticket ？ ticket好像也可以被模拟，☆☆
     * 可以取用户信息，这个不可被外部模拟，页面上取当前已认证的用户信息 ☆☆ 模拟获取，外部模拟请求不会带会话信息(不会带用户 的认证信息)
     */
    @RequestMapping(path = "/oauth2/userAccept")
    public ModelAndView userAcceptAuthorize(@RequestParam String client_id, @RequestParam String redirect_uri, String state) {
        User user = (User) request.getSession().getAttribute(UserController.USER_SESSION_KEY); // 这里本身是耦合
                                                                                               // 本系统的用户体系的

        // 授权码存储至：自动过期的map
        ModelAndView mv = new ModelAndView("redirect:" + redirect_uri);
        mv.addObject("state", state);
        mv.addObject("code", MockUtil.TimerCode.authorizerCode.generate(new AuthorizeCodeInfo(client_id, user.getId()))); // code
                                                                                                                          // 与
                                                                                                                          // 授权用户管理（这里只是为了方便模拟）
        return mv;
    }

    // 授权码：与用户相关，需要包含以下信息
    // client_id
    // user_id
    @AllArgsConstructor
    class AuthorizeCodeInfo {
        String clientId;
        Long userId;
    }

    /**
     * 2.2 // TODO 用户取消授权，重定向URI，传回用户denied [非必须] -- 内部应用调用
     */
    public void userCancelAuthorize() {

    }

    /**
     * 3. 根据授权码，签发access_token、refresh_token 等 - 供第三方调用
     */
    @RequestMapping(path = "/oauth2/access_token")
    public RetMsg accessToken(@RequestParam String code, @RequestParam String client_id, @RequestParam String client_secret,
            String redirect_uri) {
        Assert.isTrue(MockUtil.checkClient(client_id), "无效的client_id");
        OAuth2Client client = MockUtil.getClientInfo(client_id);

        // 检验授权码是否合法

        // 1. 授权码存在，且没有过期，且授权码是否归属于 client_id
        AuthorizeCodeInfo authorizeCodeInfo = (AuthorizeCodeInfo) MockUtil.TimerCode.authorizerCode.getValue(code); // 授权码一般使用1次后生效，这里未实现
        if (authorizeCodeInfo == null || authorizeCodeInfo.clientId == null || !authorizeCodeInfo.clientId.equals(client_id)) {
            return RetMsg.error("授权码已失效!");
        }

        if (!client_secret.equals(client.getClientSecret())) {
            return RetMsg.error("secret 验证失败！");
        }

        return RetMsg.success("成功", MockUtil.TimerCode.accessToken.generate(new TokenInfo(client_id, authorizeCodeInfo.userId)));
    }

    // token 应能对应的几个基本元素：
    // client_id: 客户端ID，当前token隶属的客户端
    // user_id: 用户的ID，表示当前的token来自哪个用户的授权
    // scope: 授权范围，该token允许换取的用户受保护资源范围
    // issue_time: 下发时间，用于控制token的生命周期
    @AllArgsConstructor
    class TokenInfo {
        String clientId;
        Long userId;
    }
    //

    @RequestMapping("/openapi/userName")
    public RetMsg userName() {
        String accessToken = request.getHeader("authentication-access-token");
        if (accessToken == null) {
            return RetMsg.error("access_token 不能为空！");
        }

        TokenInfo tokenInfo = (TokenInfo) MockUtil.TimerCode.accessToken.getValue(accessToken);
        if (tokenInfo == null) {
            return RetMsg.error("access_token 非法，或者已经过期！");
        }

        User user = MockUtil.userList.stream().filter(u -> u.getId().equals(tokenInfo.userId)).findAny().get();

        return RetMsg.success("success", user.getUserName());
    }
}
