package com.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static com.TotalizatorConstant.TOTALIZATOR_URL;
import static com.TotalizatorConstant.WINNER_REQUEST_ATTRIBUTE;
import static com.util.EmailRandomizer.*;

/**
 * Servlet implementation of Totalizator
 */
@WebServlet(TOTALIZATOR_URL)
public class TotalizatorServlet extends HttpServlet
{
    /**
     * Output files with obfuscated emails
     */
    public final static String DESTINATION_FILE = "obfuscatedEmails.csv";
    private static final long serialVersionUID = 5885899233778066005L;

    /**
     * Congratulation JSP
     */
    private final static String WINNER_JSP = "WEB-INF/jsp/winners.jsp";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        if (ServletFileUpload.isMultipartContent(request)) {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            try {
                FileItem file = upload.parseRequest(request).stream().findFirst().get();
                List<String> allLines;
                try (BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                    allLines = buffer.lines().collect(Collectors.toList());
                }
                ServletContext context = getServletContext();
                allLines = saveObfuscatedEmailsToFile(context.getRealPath(DESTINATION_FILE), allLines);
                request.setAttribute(WINNER_REQUEST_ATTRIBUTE, getWinnersAsList(allLines, getWinnerCount(context)));
                request.getRequestDispatcher(WINNER_JSP).forward(request, response);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.sendRedirect(request.getContextPath());
    }
}
