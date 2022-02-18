package cn.springcloud.gray.web;

import cn.springcloud.gray.request.GrayHttpTrackInfo;
import cn.springcloud.gray.request.RequestLocalStorage;
import cn.springcloud.gray.request.track.GrayTrackHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class GrayTrackFilter implements Filter {

    private RequestLocalStorage requestLocalStorage;

    private GrayTrackHolder grayTrackHolder;


    public GrayTrackFilter(GrayTrackHolder grayTrackHolder, RequestLocalStorage requestLocalStorage) {
        this.grayTrackHolder = grayTrackHolder;
        this.requestLocalStorage = requestLocalStorage;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        GrayHttpTrackInfo webTrack = new GrayHttpTrackInfo();
        HttpServletRequest req = (HttpServletRequest) request;
        System.out.println(req.getRequestURI());
        requestLocalStorage.getLocalStorageLifeCycle().initContext(GrayTrackFilter.class.getName());
        try {
            grayTrackHolder.recordGrayTrack(webTrack, new ServletHttpRequestWrapper((HttpServletRequest) request));
            requestLocalStorage.setGrayTrackInfo(webTrack);
            chain.doFilter(request, response);
        } finally {
            requestLocalStorage.removeGrayTrackInfo();
            requestLocalStorage.getLocalStorageLifeCycle().closeContext(GrayTrackFilter.class.getName());
        }
    }

    @Override
    public void destroy() {

    }
}
