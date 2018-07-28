package rebue.sbs.smx.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 让所有服务器转向时使用GET方法
 * 
 * @deprecated 此方法将所有请求都转成GET请求，具坑，请不要使用
 * 
 * @author zbz
 *
 */
//@WebFilter(urlPatterns = "/*")
@Deprecated
public class ForwardGetFilter implements Filter {

    @Override
    public void destroy() {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) request) {
            @Override
            public String getMethod() {
                return "GET";
            }
        }, response);
    }

    @Override
    public void init(FilterConfig paramFilterConfig) throws ServletException {
        // do nothing
    }

}
