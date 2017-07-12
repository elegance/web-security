package org.orh;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MockUtil {
    // user define
    // - id
    // - user_name
    // - password
    @SuppressWarnings("serial")
    public static List<User> userList = new ArrayList<User>() {
        {
            add(new User(1L, "admin", "admin123"));
            add(new User(2L, "test", "test"));
        }
    };

    public static User getUser(String userName) {
        return userList.stream().filter(o -> o.userName.equals(userName)).findFirst().get();
    }

    // oauth2 client define
    // - id
    // - client_id
    // - client_name
    // - client_secret
    @SuppressWarnings("serial")
    public static List<OAuth2Client> oauth2ClientList = new ArrayList<OAuth2Client>() {
        {
            add(new OAuth2Client(1, "2882303761517520186", "third-client-1", "63ae5dc5-8fa4-4366-8eda-29e6da172d92"));
            add(new OAuth2Client(1, "3200304793093138879", "third-client-2", "de08bf7f-daea-46d3-ae73-a6bd1621496a"));
        }
    };

    /**
     * 检查 clientId是否合法、存在
     * 
     * @param clientId
     */
    public static boolean checkClient(String clientId) {
        return oauth2ClientList.stream().filter(o -> o.clientId.equals(clientId)).count() > 0;
    }

    /**
     * 根据 clientId获取 client信息
     */
    public static OAuth2Client getClientInfo(String clientId) {
        return oauth2ClientList.stream().filter(o -> o.clientId.equals(clientId)).findFirst().get();
    }

    // code map : auto clean when expired!

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class User {
        private Long id;

        @NotEmpty
        private String userName;

        @NotEmpty
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class OAuth2Client {
        private long id;
        private String clientId;
        private String clientName;
        private String clientSecret;
    }

    static enum TimerCode {
        authorizerCode(10 * 60 * 1000), // 授权码默认有效期 10分钟
        accessToken(10 * 60 * 1000); // accessToken 30分钟有效

        private ExpirtingMap map;

        private TimerCode(long expirtingMillis) {
            map = new ExpirtingMap(expirtingMillis);
        }

        public String generate(Object value) {
            String code = UUID.randomUUID().toString().replaceAll("-", "");
            map.put(code, value);
            return code;
        }

        public Object getValue(String code) {
            return map.get(code);
        }
    }

    static class ExpirtingMap {
        private Map<String, ValWrap> map;
        private long millis;

        public ExpirtingMap(long millis) {
            System.out.println("init expirtingMap: " + millis);
            map = new ConcurrentHashMap<>();
            this.millis = millis;
            autoClean();
        }

        public void put(String key, Object value) {
            map.put(key, new ValWrap(key, value, LocalDateTime.now()));
        }

        public Object get(String key) {
            ValWrap v = map.get(key);
            return v != null ? map.get(key).value : null;
        }

        private void autoClean() {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    System.out.println("auto clean...");
                    map.entrySet().stream().forEach(v -> {
                        if (Duration.between(LocalDateTime.now(), v.getValue().createAt).toMillis() > millis) {
                            map.remove(v.getKey());
                        }
                    });
                }
            }, 0, 5 * 1000); // 每5秒检查一次
        }

        @AllArgsConstructor
        class ValWrap {
            String key;
            Object value;
            LocalDateTime createAt;
        }
    }
}
