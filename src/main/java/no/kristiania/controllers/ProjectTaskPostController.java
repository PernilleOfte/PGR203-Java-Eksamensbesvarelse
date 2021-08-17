package no.kristiania.controllers;

import no.kristiania.database.ProjectTask;
import no.kristiania.database.ProjectTaskDao;
import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectTaskPostController implements HttpController {
    private ProjectTaskDao projectTaskDao;

    public ProjectTaskPostController(ProjectTaskDao projectTaskDao) {
        this.projectTaskDao = projectTaskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        ProjectTask task = new ProjectTask();
        task.setName(requestParameter.getParameter("taskName"));
        projectTaskDao.insert(task);

        String body = "Okay";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Connection: close\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;
        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }
}

