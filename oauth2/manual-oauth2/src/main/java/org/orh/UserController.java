package org.orh;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.orh.MockUtil.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.Data;

/**
 * 资源服务器相关的认证-用普通用户名、密码登录来验证
 */
@Controller
public class UserController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;
    
    @Autowired
    protected HttpSession session;

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView("/index.btl");
        return mv;
    }

    /**
     * 网站自带的登录
     */
    @GetMapping("/toLogin")
    public ModelAndView toLogin() {
        ModelAndView mv = new ModelAndView("/identify/login.btl");
        return mv;
    }

    /**
     * 登录处理：没有callback 信息则是普通登录，跳转默认登录后的首页；否则可以指定callback 到 OAuth登录后页
     */
    @PostMapping("/login")
    @ResponseBody
    public RetMsg login(@Valid User user, BindingResult result) { // 这里为了方便，登录 与 oauth需要的登录耦合在一起
        if (result.hasErrors()) {
            return RetMsg.error("请求参数不合法", result);
        }

        boolean rs = MockUtil.userList.stream()
                .filter(u -> u.getUserName().equals(user.getUserName()))
                .allMatch(u -> u.getPassword().equals(user.getPassword()));

        if (!rs) {
            return RetMsg.error("用户名或密码错误！");

        } else {
            session.setAttribute("user", MockUtil.getUser(user.getUserName())); // 使用了session保存了会话信息
            return RetMsg.success();
        }
    }
    
    @GetMapping("/logout")
    public ModelAndView logout() {
        request.getSession().invalidate();
        return new ModelAndView("/");
    }

    @Data
    static class RetMsg {
        private String code;
        private String msg;
        private Object data;

        public static RetMsg success() {
            return success("success", null); 
        }
        public static RetMsg success(String msg, Object data) {
            RetMsg retMsg = new RetMsg();
            retMsg.code = "0000";
            retMsg.msg = msg;
            retMsg.data = data;
            return retMsg;
        }

        public static RetMsg error(String msg, BindingResult result) {
            String detail = result.getFieldErrors().stream()
                    .map(e -> e.getField() + ":" + e.getDefaultMessage())
                    .reduce("", (acc, val) -> acc + val + ";");
            return RetMsg.error(msg, detail);
        }

        public static RetMsg error(String msg, Object data) {
            RetMsg retMsg = RetMsg.error(msg);
            retMsg.data = data;
            return retMsg;
        }

        public static RetMsg error(String msg) {
            RetMsg retMsg = new RetMsg();
            retMsg.msg = msg;
            retMsg.code = "-1";
            return retMsg;
        }
    }
}
