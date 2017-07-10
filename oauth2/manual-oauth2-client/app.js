var express = require('express');
var nunjucks = require('nunjucks');
var path = require("path");

var app = express();
nunjucks.configure(path.join(__dirname), 'views', { // 设置模板文件目录
    autoescape: true,
    express: app
});

app.use(express.static('public'));
app.set('view engine', 'html'); //模板后缀名字为 html

const oauth_server = 'http://127.0.0.1:8080';
const client_id = '2882303761517520186';
const client_secret = '63ae5dc5-8fa4-4366-8eda-29e6da172d92'; // 注意：secret 只可保存在后台，否则secret泄露导致安全问题(另外 oauth server 后台也可以限定 access_token申请的白名单IP)

app.all('/account/login', (req, res, next) => {
    let query = req.query;
    let oauthProvide = query.oauth_provide;

    if (oauthProvide === 'custom') { //自定义的oauth
        // 根据 code 取 access_token 等信息
        let authorizeCode = query.code;
        res.send(`授权码：${authorizeCode}，TODO: 下一步，后台根据授权码与oauth服务商申请access_token`);
    } else {
        res.send('不支持的oauth_provide');
    }
    next();
});

app.all('/applyAccesToken', (req, res, next) => {
    let authorizeCode = req.query.authorizeCode;
    fetch(oauth_server + '/oauth2/access_token', {
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
        },
        body: `code=${authorizeCode}&client_id=${client_id}&client_secret=${client_secret}`
    }).then(oResp => {
        oResp.json().then(ret => {
            if (ret.code === '0000') {
                let accessToken = ret.data;
                res.send(`获取到的Token: ${accessToken}`);
            } else {
                res.send(JSON.stringify(ret));
            }
            
        });
        next();
    });
});


app.listen(3003, () => {
    console.log('server is running at port 3003.')
})