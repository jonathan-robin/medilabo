//package com.config;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//public class RequestContextHolderUtil {
//    public static HttpServletRequest getRequest() {
//        ServletRequestAttributes attributes =
//                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        return attributes != null ? attributes.getRequest() : null;
//    }
//}
