package no.kristiania.controllers;

import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class UpdateProjectMemberController implements HttpController {
    private ProjectMemberDao projectMemberDao;

    public UpdateProjectMemberController(ProjectMemberDao projectMemberDao) {
        this.projectMemberDao = projectMemberDao;

    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        HttpMessage response = handle(request);
        response.write(clientSocket);
    }

    public HttpMessage handle(HttpMessage request) throws SQLException {
        QueryString requestParameter = new QueryString(request.getBody());

        Integer projectMemberId = Integer.valueOf(requestParameter.getParameter("projectMemberId"));
        Integer taskId = Integer.valueOf(requestParameter.getParameter("taskId"));
        ProjectMember projectMember = projectMemberDao.retrieve(projectMemberId);
        projectMember.setTaskId(taskId);

        projectMemberDao.update(projectMember);

        HttpMessage redirect = new HttpMessage();
        redirect.setStartLine("HTTP/1.1 302 Redirect");
        redirect.getHeaders().put("Location", "http://localhost:8080/index.html");
        return redirect;

    }
}
