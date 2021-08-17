package no.kristiania.controllers;

import no.kristiania.database.ProjectTask;
import no.kristiania.database.ProjectTaskDao;
import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectTaskGetController implements HttpController {
    private ProjectTaskDao projectTaskDao;

    public ProjectTaskGetController(ProjectTaskDao projectTaskDao) {
        this.projectTaskDao = projectTaskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String body = "<ul>";
        for (ProjectTask task : projectTaskDao.list()) {
            body += "<li>" + task.getName() + "</li>";
        }

        body += "</ul>";
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/html\r\n" +
                "Connection: close\r\n" +
                "\r\n" +
                body;

        // Write the response back to the client
        clientSocket.getOutputStream().write(response.getBytes());
    }
}
