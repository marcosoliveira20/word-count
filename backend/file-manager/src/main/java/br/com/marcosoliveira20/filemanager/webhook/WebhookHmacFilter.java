package br.com.marcosoliveira20.filemanager.webhook;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class WebhookHmacFilter implements Filter {
    private final String secret;

    public WebhookHmacFilter(String secret) {
        this.secret = secret == null ? "" : secret;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (secret.isEmpty() || !(request instanceof HttpServletRequest http) || !http.getRequestURI().equals("/minio/events")) {
            chain.doFilter(request, response);
            return;
        }
        ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(http);
        chain.doFilter(wrapper, response);
        byte[] body = wrapper.getContentAsByteArray();
        String expected = DigestUtils.md5DigestAsHex((new String(body, StandardCharsets.UTF_8) + secret).getBytes(StandardCharsets.UTF_8));
        String provided = http.getHeader("X-Signature");
        if (provided == null || !provided.equalsIgnoreCase(expected)) {
            throw new ServletException("Invalid webhook signature");
        }
    }
}
