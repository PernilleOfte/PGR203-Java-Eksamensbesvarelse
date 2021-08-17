package no.kristiania.controllers;

import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectMemberOptionsController implements HttpController {
    private ProjectMemberDao projectMemberDao;

    public ProjectMemberOptionsController(ProjectMemberDao projectMemberDao) {
        this.projectMemberDao = projectMemberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }


    public String getBody() throws SQLException {
        String body = "";
        for (ProjectMember projectMember : projectMemberDao.list()) {
            body += "<option value=" + projectMember.getId() + ">" + projectMember.getFirstName() + "</option>";
        }
        return body;
    }

}
