package it.unitn.massimo.michelutti.webarch.asn3.servlets;

import it.unitn.massimo.michelutti.webarch.asn3.beans.Sheet;
import it.unitn.ronchet.Spreadsheet.SSEngine;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import java.io.IOException;

public class GetSheetServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = request.getServletContext();
        SSEngine engine= (SSEngine) context.getAttribute("Engine");
        if(engine == null){// should never run, this is just a safety net
            engine= SSEngine.getSSEngine();
            context.setAttribute("Sheet",new Sheet());
            context.setAttribute("Engine",engine);
        }
        /*Sheet sheet=(Sheet) context.getAttribute("Sheet");
        request.setAttribute("Sheet",sheet);*/
        RequestDispatcher rd=request.getRequestDispatcher("WEB-INF/sheet.jsp");
        rd.forward(request,response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }
}
