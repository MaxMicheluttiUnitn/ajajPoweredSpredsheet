package it.unitn.massimo.michelutti.webarch.asn3.servlets;

import it.unitn.massimo.michelutti.webarch.asn3.beans.Sheet;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class GetUpdateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context=request.getServletContext();
        String sheetjson=null;
        StringBuffer jb= new StringBuffer();
        String line;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }
        String req=jb.toString();
        if(!(req.startsWith("{\"time\":")&&req.endsWith("}")))
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        else {
            req=req.substring(8);
            req=req.substring(0,req.length()-1);
            try {
                long time=Long.parseLong(req);
                synchronized (context){
                    long server_time=(long)context.getAttribute("LastEdit");
                    if(server_time<time) {
                        // if last update happened after last page update
                        Sheet sheet = (Sheet) context.getAttribute("Sheet");
                        sheetjson = sheet.toJSON();
                    }else{
                        sheetjson="{\"cells\":[]}";
                    }
                }
                //System.out.println(sheetjson);
                try {
                    PrintWriter out = response.getWriter();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    out.print(sheetjson);
                    out.flush();
                } catch (Exception e) {
                    System.out.println("Error in Modify Servlet");
                }
            }catch(NumberFormatException nfe){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}
