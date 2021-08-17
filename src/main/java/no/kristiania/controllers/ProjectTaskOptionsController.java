package no.kristiania.controllers;

import no.kristiania.database.ProjectTask;
import no.kristiania.database.ProjectTaskDao;
import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ProjectTaskOptionsController implements HttpController {
    private ProjectTaskDao projectTaskDao;

    public ProjectTaskOptionsController(ProjectTaskDao projectTaskDao) {
        this.projectTaskDao = projectTaskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() throws SQLException {
        String body = "";
        for (ProjectTask projectTask : projectTaskDao.list())
            body += "<option value=" + projectTask.getId() + ">" + projectTask.getName() + "</option>";
        return body;
    }
}
