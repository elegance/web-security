var express = require('express');
var nunjucks = require('nunjucks');
var path = require('path');
var fetch = require('node-fetch');

var app = express();
nunjucks.configure(path.join(__dirname, 'views'), { // 设置模板文件目录
    autoescape: true,
    express: app,
    watch: true
});

app.use(express.static('public'));
app.set('view engine', 'html'); //模板后缀名字为 html

const oauth_server = 'http://127.0.0.1:8080';
const client_id = '2882303761517520186';
const client_secret = '63ae5dc5-8fa4-4366-8eda-29e6da172d92'; // 注意：secret 只可保存在后台，否则secret泄露导致安全问题(另外 oauth server 后台也可以限定 access_token申请的白名单IP)

app.all('/account/login', (req, res, next) => {
    let {code, oauth_provide} = req.query;

    res.render('showAuthorizeCode.html', {
        code,
        oauth_provide
    })
});

app.all('/applyAccessToken', (req, res, next) => {
    let authorizeCode = req.query.authorizeCode;
    console.log(`code=${authorizeCode}&client_id=${client_id}&client_secret=${client_secret}`);

    fetch(oauth_server + '/oauth2/access_token', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
        },
        body: `code=${authorizeCode}&client_id=${client_id}&client_secret=${client_secret}`
    }).then(oResp => {
        oResp.json().then(ret => {
            console.log(ret);
            res.send(ret);
        });
    });
});


app.listen(3003, () => {
    console.log('server is running at port 3003.')
})