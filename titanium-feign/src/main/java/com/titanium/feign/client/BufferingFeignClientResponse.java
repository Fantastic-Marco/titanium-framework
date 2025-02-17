package com.titanium.feign.client;

import feign.Response;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.util.Collection;
import java.util.Map;

public class BufferingFeignClientResponse implements Closeable {
    public Response response;
    public byte[] body;

    public BufferingFeignClientResponse(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return this.response;
    }

    public int status() {
        return this.response.status();
    }

    public Map<String, Collection<String>> headers() {
        return this.response.headers();
    }

    public String body() throws IOException {
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(getBody())) {
            char[] tmp = new char[1024];
            int len;
            while ((len = reader.read(tmp, 0, tmp.length)) != -1) {
                sb.append(new String(tmp, 0, len));
            }
        }
        return sb.toString();
    }

    public InputStream getBody() throws IOException {
        if (this.body == null) {
            if (this.response.body() != null) {
                this.body = StreamUtils.copyToByteArray(this.response.body().asInputStream());
            } else {
                this.body = new byte[0];
            }
        }
        return new ByteArrayInputStream(this.body);
    }

    @Override
    public void close() {
        this.response.close();
    }
}
