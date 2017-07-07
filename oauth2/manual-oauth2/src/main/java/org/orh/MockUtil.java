package org.orh;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MockUtil {
    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString());
    }

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

    static enum AuthorizerCode {
        code;

    }
}
