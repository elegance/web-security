package org.orh;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.orh.MockUtil.OAuth2Client;
import org.orh.MockUtil.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class OAuth2Controller {

    @Autowired
    protected HttpServletRequest request;

    /**
     * 1. 向用户申请授权 - 供第三方/外部调用
     */
    @GetMapping(path = "/oauth2/authorize") // 一般此地址出现在 —— 三方应用的 view上，用户可见
    public ModelAndView authorize(@RequestParam String client_id, @RequestParam String redirect_uri, String state) {
        // TODO 导向“用户授权界面”
        // * 如果用户还未进行认证，也就是服务器根本不知道是谁在请求，那么就需要先登录进行认证
        // * 如果用户已经通过了认证，那么就导向 授权界面
        // * optimization feature: 有时候，在短时间内，如果用户已经授权过该应用，就不再导向用户授权界面，直接发放授权码重定向URI
        
        Assert.isTrue(MockUtil.checkClient(client_id), "无效的client_id");

        ModelAndView mv = null;

        User user = (User) request.getSession().getAttribute("user"); // 这里本身是耦合 本系统的用户体系的

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
     * 这里的接口谁都有权来访问，岂不是可以跳过用户的授权，直接申请授权码？ 确保请求来自  "/oauth2/authorize"，先发一个 ticket ？ ticket 模拟获取，外部模拟请求不会带会话信息
     */
    @RequestMapping(path = "/oauth2/userAccept")
    public ModelAndView userAcceptAuthorize(@RequestParam String client_id, @RequestParam String redirect_uri, String state) {
        // 授权码存储至：自动过期的map
        ModelAndView mv = new ModelAndView("redirect:" + redirect_uri);
        mv.addObject("state", state);
        mv.addObject("code", UUID.randomUUID().toString().replaceAll("-", ""));
        return mv;
    }

    /**
     * 2.2 // TODO 用户取消授权，重定向URI，传回用户denied [非必须] -- 内部应用调用
     */
    public void userCancelAuthorize() {

    }

    /**
     * 3. 根据授权码，签发access_token、refresh_token 等 - 供第三方调用
     */
    @GetMapping(path = "/oauth2/access_token")
    public void accessToken() {
        // 检验授权码是否合法
        // 1. 授权码存在，且没有过期
        // 2. 授权码是否归属于 client_id
    }
}
