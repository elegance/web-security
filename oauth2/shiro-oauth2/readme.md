关于本项目
=================

系统采用基础采用Spring Boot + Shiro + H2，主要是使用 Shiro 实现OAuth2的演示。

### 基本要求（Requirements）

	* ORACLE JDK 8
	* Apache Maven

### 运行

	mvn spring-boot:run

### 基本表结构

* 使用了嵌入 H2 作为的数据源，jpa根据配置文件`application.properties`会自动创建数据文件

<table>
    <tbody>
        <tr class="firstRow">
            <td valign="top" rowspan="1" colspan="4" style="word-break: break-all;">
                用户（oauth2_user）<br/>
            </td>
        </tr>
        <tr>
            <td width="190" valign="top" style="word-break: break-all;">
                名称<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                类型<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                长度<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                描述<br/>
            </td>
        </tr>
        <tr>
            <td width="190" valign="top" style="word-break: break-all;">
                id<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                bigint
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                10
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                编号 主键<br/>
            </td>
        </tr>
        <tr>
            <td width="190" valign="top" style="word-break: break-all;">
                username<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                varchar<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                100
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                用户名<br/>
            </td>
        </tr>
        <tr>
            <td width="190" valign="top" style="word-break: break-all;">
                password<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                varchar<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                100<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                密码<br/>
            </td>
        </tr>
        <tr>
            <td width="190" valign="top" style="word-break: break-all;">
                salt<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                varchar
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                50
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                盐<br/>
            </td>
        </tr>
    </tbody>
</table>

<table>
    <tbody>
        <tr class="firstRow">
            <td valign="top" rowspan="1" colspan="4" style="word-break: break-all;">
                客户端（oauth2_client）<br/>
            </td>
        </tr>
        <tr>
            <td width="190" valign="top" style="word-break: break-all;">
                名称<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                类型<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                长度<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                描述<br/>
            </td>
        </tr>
        <tr>
            <td width="190" valign="top" style="word-break: break-all;">
                id<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                bigint
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                10
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                编号 主键<br/>
            </td>
        </tr>
        <tr>
            <td width="190" valign="top" style="word-break: break-all;">
                client_name<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                varchar<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                100
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                客户端名称<br/>
            </td>
        </tr>
        <tr>
            <td width="190" valign="top" style="word-break: break-all;">
                client_id<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                varchar<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                100<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                客户端id
            </td>
        </tr>
        <tr>
            <td width="190" valign="top" style="word-break: break-all;">
                client_secret<br/>
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                varchar
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                100
            </td>
            <td width="190" valign="top" style="word-break: break-all;">
                客户端安全key
            </td>
        </tr>
    </tbody>
</table>

### 测试接口与测试数据生成
你可以使用`postman`导入[shiro-oauth2.postman_collection](shiro-oauth2.postman_collection.json)文件，可以创建User、Client。