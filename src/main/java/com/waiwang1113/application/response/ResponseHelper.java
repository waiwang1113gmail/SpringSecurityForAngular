package com.waiwang1113.application.response;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Helper methods to send responses
 * @author wanwe17
 *
 */
public class ResponseHelper {
	 private static final ObjectMapper mapper = new ObjectMapper();
	 
	public static void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        PrintWriter writer = response.getWriter(); 
        writer.write(mapper.writeValueAsString(new ErrorResponse(status, message)));
        writer.flush();
        writer.close();
    }


    public static void sendResponse(HttpServletResponse response, int status, Object object) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(mapper.writeValueAsString(object));
        response.setStatus(status);
        writer.flush();
        writer.close();
    }
}
