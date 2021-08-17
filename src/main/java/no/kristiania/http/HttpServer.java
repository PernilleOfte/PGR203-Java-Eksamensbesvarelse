package no.kristiania.http;

import no.kristiania.controllers.*;
import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import no.kristiania.database.ProjectTaskDao;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class HttpServer {

    private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private Map<String, HttpController> controllers;

    private ProjectMemberDao projectMemberDao;
    private ServerSocket serverSocket;

    public HttpServer(int port, DataSource dataSource) throws IOException {
        projectMemberDao = new ProjectMemberDao(dataSource);
        ProjectMemberController projectMemberController = new ProjectMemberController(projectMemberDao);
        ProjectTaskDao projectTaskDao = new ProjectTaskDao(dataSource);
        controllers = Map.of(
                "/newTask", new ProjectTaskPostController(projectTaskDao),
                "/tasks", new ProjectTaskGetController(projectTaskDao),
                "/projectMemberOptions", new ProjectMemberOptionsController(projectMemberDao),
                "/taskOptions", new ProjectTaskOptionsController(projectTaskDao),
                "/updateProject", new UpdateProjectMemberController(projectMemberDao),
                "/memberTaskList", new UpdateProjectMemberGetController(projectMemberDao),
                "/statusList", new ProjectTaskStatusController(),
                "/updateStatus", new UpdateProjectStatusController(projectTaskDao),
                "/members", new ProjectMemberController.PostControllerProject(projectMemberDao),
                "/projectMember", new ProjectMemberController.GetControllerProject(projectMemberDao)
        );

        // Opens a entry point to our program for network clients
        ServerSocket serverSocket = new ServerSocket(port);

        // new Threads executes the code in a separate "thread", that is: In parallel
        new Thread(() -> { // anonymous function with code that will be executed in parallel
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    // accept waits for a client to try to connect - blocks
                    handleRequest(clientSocket);
                } catch (IOException | SQLException e) {
                    // If something went wrong - print out exception and try again
                    e.printStackTrace();
                }
            }
        }).start();// Start the threads, so the code inside executes without block the current thread

    }


    private void handleRequest(Socket clientSocket) throws IOException, SQLException {
        HttpMessage request = new HttpMessage(clientSocket);
        String requestLine = request.getStartLine();

        System.out.println("REQUEST " + requestLine);
        // Example "GET /echo?body=hello HTTP/1.1"

        // Example GET, POST, PUT, DELETE etc
        String requestMethod = requestLine.split(" ")[0];
        String requestTarget = requestLine.split(" ")[1];
        // Example "/echo?body=hello"

        int questionPos = requestTarget.indexOf('?');

        String requestPath = questionPos != -1 ? requestTarget.substring(0, questionPos) : requestTarget;

        if (requestPath.equals("/echo")) {
            handleEchoRequest(clientSocket, requestTarget, questionPos);
        } else {
            HttpController controller = controllers.get(requestPath);
            if (controller != null) {
                controller.handle(request, clientSocket);
            } else {
                handleFileRequest(clientSocket, requestPath);
            }
        }
    }


    private void handleFileRequest(Socket clientSocket, String requestPath) throws IOException {
        //Lager en inputStream på det som er pakket inn i jar filen
        try (InputStream inputStream = getClass().getResourceAsStream(requestPath)) {
            if (inputStream == null) {
                String body = requestPath + " does not exist";
                String response = "HTTP/1.1 404 not Found\r\n" +
                        "Content-Length: " + body.length() + "\r\n" +
                        "\r\n" +
                        body;
                clientSocket.getOutputStream().write(response.getBytes());
                return;

            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            inputStream.transferTo(buffer);


            String contentType = "text/plain";
            if (requestPath.endsWith(".html")) {
                contentType = "text/html";
            }
            if (requestPath.endsWith(".css")) {
                contentType = "text/css";
            }

            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Length: " + buffer.toByteArray().length + "\r\n" +
                    "Connection: close\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "\r\n";

            clientSocket.getOutputStream().write((response.getBytes()));
            clientSocket.getOutputStream().write(buffer.toByteArray());
        }

    }


    private void handleEchoRequest(Socket clientSocket, String requestTarget, int questionPos) throws IOException {
        String statusCode = "200";
        String body = "Hello <strong>World</strong>!";
        if (questionPos != -1) {
            QueryString queryString = new QueryString(requestTarget.substring(questionPos + 1));
            if (queryString.getParameter("status") != null) {
                statusCode = queryString.getParameter("status");
            }
            if (queryString.getParameter("body") != null) {
                body = queryString.getParameter("body");
            }
        }
        String response = "HTTP/1.1 " + statusCode + " OK\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                body;
        clientSocket.getOutputStream().write(response.getBytes());

    }


    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader("pgr203.properties")) {
            properties.load(fileReader);
        }

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(properties.getProperty("dataSource.url"));
        dataSource.setUser(properties.getProperty("dataSource.username"));
        dataSource.setPassword(properties.getProperty("dataSource.password"));
        Flyway.configure().dataSource(dataSource).load().migrate();
        logger.info("Using database {}", dataSource.getUrl());

        HttpServer server = new HttpServer(8080, dataSource);
        logger.info("Started on http://localhost:{}/index.html", 8080);

    }


    public List<ProjectMember> getProject() throws SQLException {
        return projectMemberDao.list();
    }
}
