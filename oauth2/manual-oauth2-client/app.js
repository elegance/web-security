var express = require('express');

var app = express();

app.use(express.static('public'));

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


app.listen(3003, () => {
    console.log('server is running at port 3003.')
})