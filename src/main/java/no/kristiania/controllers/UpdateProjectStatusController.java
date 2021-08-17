package no.kristiania.controllers;

import no.kristiania.database.ProjectTask;
import no.kristiania.database.ProjectTaskDao;
import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateProjectStatusController implements HttpController {
    private ProjectTaskDao projectTaskDao;

    public UpdateProjectStatusController(ProjectTaskDao projectTaskDao) {
        this.projectTaskDao = projectTaskDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = handle(request);
        response.write(clientSocket);
    }

    public HttpMessage handle(HttpMessage request) throws SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        Integer taskId = Integer.valueOf(requestParameter.getParameter("taskId"));
        Integer statusId = Integer.valueOf(requestParameter.getParameter("statusId"));
        ProjectTask projectTask = projectTaskDao.retrieve(taskId);
        projectTask.setStatus(ProjectTaskStatusController.statuses.get(statusId));

        projectTaskDao.update(projectTask);

        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/index.html");
        return redirect;

    }
}
