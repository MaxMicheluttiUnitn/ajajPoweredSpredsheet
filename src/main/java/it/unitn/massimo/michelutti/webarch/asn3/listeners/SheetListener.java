package it.unitn.massimo.michelutti.webarch.asn3.listeners;

import it.unitn.massimo.michelutti.webarch.asn3.beans.Sheet;
import it.unitn.ronchet.Spreadsheet.SSEngine;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebListener
public class SheetListener implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {

    public SheetListener() {
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        /* This method is called when the servlet context is initialized(when the Web application is deployed). */
        ServletContext context = sce.getServletContext();
        SSEngine engine= SSEngine.getSSEngine();
        Sheet sheet=new Sheet();
        long unixTime = System.currentTimeMillis() / 1000L;
        context.setAttribute("Sheet",sheet);
        context.setAttribute("Engine",engine);
        context.setAttribute("LastEdit",unixTime);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        /* This method is called when the servlet Context is undeployed or Application Server shuts down. */
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        /* Session is created. */
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        /* Session is destroyed. */
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is added to a session. */
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is removed from a session. */
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent sbe) {
        /* This method is called when an attribute is replaced in a session. */
    }
}
