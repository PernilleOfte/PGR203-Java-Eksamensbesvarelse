package no.kristiania.controllers;

import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;
import no.kristiania.http.QueryString;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

public class ProjectMemberController implements HttpController {

    ProjectMemberDao projectMemberDao;

    public ProjectMemberController(ProjectMemberDao projectMemberDao) {
        this.projectMemberDao = projectMemberDao;
    }

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
        System.out.println("Hit the default handler..");
    }

    public static class PostControllerProject extends ProjectMemberController implements HttpController {

        public PostControllerProject(ProjectMemberDao projectMemberDao) {
            super(projectMemberDao);
        }

        @Override
        public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
            QueryString requestParameter = new QueryString(request.getBody());
            ProjectMember projectMember = new ProjectMember();
            projectMember.setFirstName(requestParameter.getParameter("first_name"));
            projectMember.setLastName(requestParameter.getParameter("last_name"));
            projectMember.setEmail(requestParameter.getParameter("email"));
            projectMemberDao.insert(projectMember);
            String body = "Okay";
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "\r\n" +
                    body;
            // Write the response back to the client
            clientSocket.getOutputStream().write(response.getBytes());

        }

    }

    public static class GetControllerProject extends ProjectMemberController implements HttpController {

        public GetControllerProject(ProjectMemberDao projectMemberDao) {
            super(projectMemberDao);
        }

        @Override
        public void handle(HttpMessage request, Socket clientSocket) throws IOException, SQLException {
            String requestLine = request.getStartLine();
            String requestTarget = requestLine.split(" ")[1];

            int questionPos = requestTarget.indexOf('?');

            String body = "<ul>";
            List<ProjectMember> projectMembers;
            if (questionPos == -1) {
                projectMembers = projectMemberDao.list();
            } else {
                QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
                int taskId = Integer.parseInt(queryString.getParameter("taskId"));
                projectMembers = projectMemberDao.listByTaskId(taskId);
            }
            for (ProjectMember projectMember : projectMembers) {
                body += "<li>" + projectMember.getFirstName() + " " + projectMember.getLastName() + " " + "- " + projectMember.getEmail() + " " + (projectMember.getCurrentStatus() != null ? projectMember.getCurrentStatus() : "") + "</li>";
            }
            body += "</ul>";
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + body.length() + "\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" +
                    body;
            clientSocket.getOutputStream().write(response.getBytes());


        }
    }
}