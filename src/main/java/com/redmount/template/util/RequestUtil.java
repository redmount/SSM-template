package com.redmount.template.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class RequestUtil {
    public static String getHeaderStringFromRequest(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Enumeration enu = request.getHeaderNames();
        String headerName;
        while (enu.hasMoreElements()) {//以此取出头信息
            headerName = enu.nextElement().toString();
            sb.append(headerName);
            sb.append(":");
            sb.append(request.getHeader(headerName));
            sb.append(",");
        }
        return sb.toString();
    }
}
