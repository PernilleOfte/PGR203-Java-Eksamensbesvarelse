package no.kristiania.controllers;

import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateProjectMemberGetController implements HttpController {

    private ProjectMemberDao projectMemberDao;


    public UpdateProjectMemberGetController(ProjectMemberDao projectMemberDao) {
        this.projectMemberDao = projectMemberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        String body = "<ul>";
        for (ProjectMember member : projectMemberDao.list()) {
            body += "<li>" + member.getFirstName() + member.getLastName() + "</li>";
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

