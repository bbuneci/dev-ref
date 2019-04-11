package io.keikai.devref.web;

import io.keikai.devref.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

import static io.keikai.devref.Configuration.fileAppMap;

@WebServlet("/case/*")
public class CaseServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(request, resp);
        String fileName = request.getPathInfo().substring(1);
        Class appClass = fileAppMap.get(fileName);
        if (appClass == null) {
            request.getRequestDispatcher("/notfound.jsp").forward(request, resp);
        } else {
            try {
                KeikaiCase keikaiCase = (KeikaiCase) appClass.newInstance();
                keikaiCase.init(keikaiServerAddress);
                // pass the anchor DOM element id for rendering keikai
                String keikaiJs = keikaiCase.getJavaScriptURI("spreadsheet");
                // store as an attribute to be accessed by EL on a JSP
                request.setAttribute(Configuration.KEIKAI_JS, keikaiJs);
                request.getRequestDispatcher("/mycase/case.jsp").forward(request, resp);
                keikaiCase.run();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}