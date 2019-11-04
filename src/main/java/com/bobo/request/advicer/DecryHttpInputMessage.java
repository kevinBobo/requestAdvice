package com.bobo.request.advicer;

import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
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
        return IoUtil.toStream(SecurityUtils.decry(encryBody),StandardCharsets.UTF_8);
    }


}
