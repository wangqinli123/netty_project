package com.itheima.netty.servlets;

import com.itheima.netty.http.LdRequest;
import com.itheima.netty.http.LdResponse;
import com.itheima.netty.http.LdServlet;

public class MyServlet extends LdServlet {

    @Override
    public void doGet(LdRequest request, LdResponse response) {
        try {
            response.write(request.getParameter("name"));
        }catch (Exception e){

        }

    }

    @Override
    public void doPost(LdRequest request, LdResponse response) {
        doGet(request,response);
    }
}
