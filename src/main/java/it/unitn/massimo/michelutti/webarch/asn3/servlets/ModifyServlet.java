package it.unitn.massimo.michelutti.webarch.asn3.servlets;

import com.google.gson.Gson;
import it.unitn.massimo.michelutti.webarch.asn3.beans.CellEdit;
import it.unitn.massimo.michelutti.webarch.asn3.beans.Sheet;
import it.unitn.ronchet.Spreadsheet.Cell;
import it.unitn.ronchet.Spreadsheet.SSEngine;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.StringTokenizer;

//@WebServlet(name = "ModifyServlet", value = "/ModifyServlet")
public class ModifyServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        StringBuffer jb = new StringBuffer();
        String line = null;
        String cell=null;
        String content=null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }
        String req=jb.toString();
        if(!req.contains("\"content\"")||!req.contains("\"cell\""))
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        else {
            //PARSING JSON, THIS IS NOT A PERFECT JSON PARSER, BUT IT DOES THE JOB FOR NON-ARRAY NON-HIERARCHICAL JSON OBJECTS
            System.out.println(req);
            /*try {
                Gson gson = new Gson();
                CellEdit myobject = gson.fromJson(req, CellEdit.class);
                System.out.println(myobject);
            }catch(Exception e){
                e.printStackTrace();
            }*/

            StringTokenizer st=new StringTokenizer(req);
            int counter=0;
            String last_property=null;
            while(st.hasMoreElements()){
                String elem=st.nextToken("\"");
                if(counter%4==1){
                    last_property=elem;
                }
                if(counter%4==3){
                    if(last_property.equals("cell")){
                        cell=elem;
                    }else if(last_property.equals("content")){
                        content=elem;
                    }
                }
                counter+=1;
            }
            if(cell==null||content==null){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }else {
                //System.out.println(req);
                String jsonarray="{\"cells\":[";
                synchronized (context) {
                    SSEngine engine= (SSEngine) context.getAttribute("Engine");
                    Sheet sheet=(Sheet)context.getAttribute("Sheet");
                    Set<Cell> modified = engine.modifyCell(cell, content);
                    if(modified!=null&&!modified.isEmpty()){
                        context.setAttribute("LastEdit",System.currentTimeMillis() / 1000L);
                        for (Cell c : modified) {
                            String formula = c.getFormula();
                            int value = c.getValue();
                            String tos = c.toString();
                            int column = tos.charAt(4) - 'A';
                            int row = tos.charAt(5) - '1';
                            sheet.setValue(row, column, value);
                            sheet.setFormula(row, column, formula);
                            //System.out.println(column + " " + row + " " + formula + " " + value);
                            jsonarray += "{\"cell\":\"" + tos.charAt(4) + tos.charAt(5) + "\"," +
                                    "\"value\":" + value + "," +
                                    "\"formula\":\"" + formula + "\"},";
                        }
                        //remove last ","
                        jsonarray=jsonarray.substring(0, jsonarray.length() - 1);
                    }else if(modified==null){
                        jsonarray += "{\"cell\":\"" + cell + "\"," +
                                "\"formula\":\"=ERROR(" + content + ")\"}";
                    }
                }
                jsonarray+="]}";


                try {
                    PrintWriter out = response.getWriter();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    out.print(jsonarray);
                    out.flush();
                } catch (Exception e) {
                    System.out.println("Error in Modify Servlet");
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }
}
