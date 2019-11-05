# 起源



引起我这个思考的是群里一个水友的公司要求请求报文加密,所以他想统一处理加密报文,但是不知道用什么方式好,我第一的反应就是filter,interceptor,aop,然后看了一下spring mvc提供了功能接口RequestBodyAdvice

但是实现的话他们那种方法最好呢?

于是这篇文章就诞生了!

> **注意**:代码内容已加解密为案例,但是真实场景中,这种安全的最好方案是https,此处应为水友公司老板不采用https方案所以诞生了这个思考

# aop

使用aop的话想法很美好但是,实现出来却不太好,因为aop环绕的时候已经需要把请求体的参数映射成你要执行的对象,这时候代码已经开始报错,迎来了残酷的现实.

所以aop暂时不可行,不推荐



# interceptor

spring mvc的拦截器,他可以通过拦截request拿到所有的请求参数,但是拦截器的天然作用是用来拦截请求,或者返回,读取完请求的流之后无法将其重新设置进去,也不太可行

所以interceptor暂时不可行,不推荐



#filter

filter作为过滤器用来过滤请求,定然是可以的

话不多说,先上代码

```java
//@WebFilter
@Slf4j
public class SecurityFilter implements Filter {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        SecurityServletRequest securityServletRequest = new SecurityServletRequest((HttpServletRequest) servletRequest);
        SecurityServletResponse securityServletResponse = new SecurityServletResponse((HttpServletResponse) servletResponse);
        filterChain.doFilter(securityServletRequest, securityServletResponse);
        ServletOutputStream out = servletResponse.getOutputStream();
        byte[] content = securityServletResponse.getContent();
        JSONObject jsonObject = JSON.parseObject(new String(content));
        jsonObject.put("data",SecurityUtils.encry(jsonObject.getString("data")));
        out.write(jsonObject.toJSONString().getBytes());
        out.flush();
    }
}
```

 

```java
public class SecurityServletRequest  extends HttpServletRequestWrapper {

    public SecurityServletRequest(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ServletInputStream inputStream = super.getInputStream();
        String encryBody = IoUtil.read(inputStream, StandardCharsets.UTF_8);
        JSONObject jsonObject = JSON.parseObject(encryBody);
        jsonObject.put("data", JSON.parse(SecurityUtils.decry(jsonObject.getString("data"))));
        String s = jsonObject.toJSONString();
        return new BodyInputStream(s.getBytes());
    }

    private static class BodyInputStream extends ServletInputStream {

        private final InputStream delegate;

        public BodyInputStream(byte[] body) {
            this.delegate = new ByteArrayInputStream(body);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int read() throws IOException {
            return this.delegate.read();
        }

        @Override
        public int read(@NotNull byte[] b, int off, int len) throws IOException {
            return this.delegate.read(b, off, len);
        }

        @Override
        public int read(@NotNull byte[] b) throws IOException {
            return this.delegate.read(b);
        }

        @Override
        public long skip(long n) throws IOException {
            return this.delegate.skip(n);
        }

        @Override
        public int available() throws IOException {
            return this.delegate.available();
        }

        @Override
        public void close() throws IOException {
            this.delegate.close();
        }

        @Override
        public synchronized void mark(int readlimit) {
            this.delegate.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            this.delegate.reset();
        }

        @Override
        public boolean markSupported() {
            return this.delegate.markSupported();
        }
    }
}
```

```java
public class SecurityServletResponse extends HttpServletResponseWrapper {

    private ByteArrayOutputStream buffer;

    private ServletOutputStream out;

    public SecurityServletResponse(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
        buffer = new ByteArrayOutputStream();
        out = new WrapperOutputStream(buffer);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return out;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (out != null) {
            out.flush();
        }
    }

    public byte[] getContent() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    class WrapperOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream bos;

        public WrapperOutputStream(ByteArrayOutputStream bos) {
            this.bos = bos;
        }

        @Override
        public void write(int b) throws IOException {
            bos.write(b);
        }

        @Override
        public boolean isReady() {

            // TODO Auto-generated method stub
            return false;

        }

        @Override
        public void setWriteListener(WriteListener listener) {

        }

    }


}
```

这里我请求响应都尝试做了加密,因为request的流全程只可以读取一次,我这里进行了包装,通过重写接口让数据读取或者写入到我提供的stream中,最后我用真正的流在filter中写入或者写出

缺点是代码过程中产生了比较多的冗余数据,对性能要求高的话还是有点不优雅



# RequestBodyAdvice

下面是本文章的重头戏,既然预留了接口,当然他干的事情就是你想要的

所以没错,他就是最优雅的,推荐

```java
@Slf4j
public class DecryptRequestAdvice implements RequestBodyAdvice {

    /**
     * 是否走这个拦截器，可以根据自己定义注解等看情况解析
     *
     * @param methodParameter
     * @param type
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    /**
     * controller读取之前
     *
     * @param httpInputMessage
     * @param methodParameter
     * @param type
     * @param aClass
     * @return
     * @throws IOException
     */
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        log.info("controller读取body之前");
        return new DecryHttpInputMessage(httpInputMessage);
    }

    @Override
    public Object afterBodyRead(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        log.info("controller读取之后");
        return o;
    }

    @Override
    public Object handleEmptyBody(Object o, HttpInputMessage httpInputMessage, MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        log.info("这是一个空body");
        return o;
    }
```



```java
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
```



整体比较优雅,没有太多冗余的数据



# 总结

| 名字              | 效果                            | 推荐   |
| ----------------- | ------------------------------- | ------ |
| aop               | 拦截之前已经出现错误            | 不推荐 |
| requestbodyadvice | 专门为请求body处理提供,简单高效 | 推荐   |
| interceptor       | 负责拦截请求,不适合处理数据     | 不推荐 |
| filter            | 可以实现,冗余数据比较多         | 一般   |

