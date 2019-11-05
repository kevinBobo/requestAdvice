package com.bobo.request.advicer;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bobo.request.util.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Data
@AllArgsConstructor
public class DecryHttpInputMessage implements HttpInputMessage {

    private HttpInputMessage httpInputMessage;

    @Override
    public InputStream getBody() throws IOException {
        return decry();
    }

    @Override
    public HttpHeaders getHeaders() {
        return httpInputMessage.getHeaders();
    }

    private InputStream decry() throws IOException {
        String encryBody = IoUtil.read(httpInputMessage.getBody(), StandardCharsets.UTF_8);
        JSONObject jsonObject = JSON.parseObject(encryBody);
        jsonObject.put("data", JSON.parse(SecurityUtils.decry(jsonObject.getString("data"))));
        String s = jsonObject.toJSONString();
        return IoUtil.toStream(s, StandardCharsets.UTF_8);
    }


}
