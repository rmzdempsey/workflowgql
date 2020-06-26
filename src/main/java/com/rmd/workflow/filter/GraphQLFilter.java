package com.rmd.workflow.filter;

import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;


@Component
@Order(2)
@Slf4j
public class GraphQLFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        if(false){
            chain.doFilter(request, response);
        }
        else {

            HttpServletRequest req = (HttpServletRequest) request;
            ResettableStreamHttpServletRequest wrappedRequest = new ResettableStreamHttpServletRequest(
                    (HttpServletRequest) request);
            // wrappedRequest.getInputStream().read();
            String body = IOUtils.toString(wrappedRequest.getReader());
            log.info(
                    "Logging Request  {} : {}, {}", req.getMethod(),
                    req.getRequestURI(),
                    body);
            //auditor.audit(wrappedRequest.getRequestURI(),wrappedRequest.getUserPrincipal(), body);
            wrappedRequest.resetInputStream();
            chain.doFilter(wrappedRequest, response);
        }

//        HttpServletRequest req = (HttpServletRequest) request;
//        HttpServletResponse res = (HttpServletResponse) response;
//        log.info(
//                "Logging Request  {} : {}", req.getMethod(),
//                req.getRequestURI());
//        chain.doFilter(request, response);
//        log.info(
//                "Logging Response :{}",
//                res.getContentType());
    }

//    private String readerToString(BufferedReader br)throws IOException{
//        StringBuilder sb = new StringBuilder();
//        String line = null;
//        while( (line=br.readLine())!=null){
//            if(sb.length()>0){
//                sb.append("\n");
//            }
//            sb.append(line);
//        }
//        return sb.toString();
//    }

    private static class ResettableStreamHttpServletRequest extends
            HttpServletRequestWrapper {

        private byte[] rawData;
        private HttpServletRequest request;
        private ResettableServletInputStream servletStream;

        public ResettableStreamHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.request = request;
            this.servletStream = new ResettableServletInputStream();
        }


        public void resetInputStream() {
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return servletStream;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return new BufferedReader(new InputStreamReader(servletStream));
        }


        private class ResettableServletInputStream extends ServletInputStream {

            private InputStream stream;

            @Override
            public int read() throws IOException {
                return stream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }
        }
    }

}
