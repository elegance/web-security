## 目录结构
* [manual-oauth2](manual-oauth2) OAuth2.0 的服务提供者
* [manual-oauth2-client](manual-oauth2-client)  模拟三方应用，测试上面的OAuth2.0服务

## 手动实现班测试(未完整)
1. 启动后台服务（java Spring Boot 应用）
```
$ cd manual-oauth2
$ mvn clean spring-boot:run 
```
2. 启动模拟的三方应用（nodejs express 应用）
```
$ cd ../manual-oauth2-client
$ ndoe app.js
```
3. 打开连接开始测试吧

[http://192.168.2.11:3003/](http://192.168.2.11:3003/)