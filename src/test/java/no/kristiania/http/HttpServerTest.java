package no.kristiania.http;

import no.kristiania.database.ProjectMember;
import no.kristiania.database.ProjectMemberDao;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpServerTest {

    private JdbcDataSource dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");

        Flyway.configure().dataSource(dataSource).load().migrate();
    }


    @Test
    void shouldReturnSuccessfulErrorCode() throws IOException {
        new HttpServer(10001, dataSource);
        HttpClient client = new HttpClient("localhost", 10001, "/echo");
        assertEquals(200, client.getStatusCode());
    }

    @Test
    void shouldReturnUnsuccessfulErrorCode() throws IOException {
        new HttpServer(10002, dataSource);
        HttpClient client = new HttpClient("localhost", 10002, "/echo?status=404");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnHttpHeaders() throws IOException {
        new HttpServer(10003, dataSource);
        HttpClient client = new HttpClient("localhost", 10003, "/echo?body=HelloWorld");
        assertEquals("10", client.getResponseHeader("Content-Length"));
    }

    @Test
    void shouldReturnResponseBody() throws IOException {
        new HttpServer(10004, dataSource);
        HttpClient client = new HttpClient("localhost", 10004, "/echo?body=HelloWorld");
        assertEquals("HelloWorld", client.getResponseBody());
    }

    @Test
    void shouldReturnFileFromDisk() throws IOException {
        HttpServer server = new HttpServer(10005, dataSource);
        File documentRoot = new File("target/test-classes");

        String fileContent = "Hello World " + new Date();
        Files.writeString(new File(documentRoot, "test.txt").toPath(), fileContent);

        HttpClient client = new HttpClient("localhost", 10005, "/test.txt");
        assertEquals(fileContent, client.getResponseBody());
        assertEquals("text/plain", client.getResponseHeader("Content-Type"));

    }

    @Test
    void shouldReturn404onMissingFile() throws IOException {
        HttpServer server = new HttpServer(10006, dataSource);
        HttpClient client = new HttpClient("localhost", 10006, "/missingFile");
        assertEquals(404, client.getStatusCode());
    }

    @Test
    void shouldReturnCorrectContentType() throws IOException {
        HttpServer server = new HttpServer(10007, dataSource);
        File documentRoot = new File("target/test-classes");

        Files.writeString(new File(documentRoot, "projectMembers.txt").toPath(), "<h2>Hello World</h2>");

        HttpClient client = new HttpClient("localhost", 10007, "/index.html");
        assertEquals("text/html", client.getResponseHeader("Content-Type"));
    }

    @Test
    void shouldPostNewMember() throws IOException, SQLException {
        HttpServer server = new HttpServer(10008, dataSource);
        HttpClient client = new HttpClient("localhost", 10008, "/members", "POST", "first_name=Andreas&last_name=nilsen");
        assertEquals(200, client.getStatusCode());
        assertThat(server.getProject())
                .extracting(projectMember -> projectMember.getFirstName())
                .contains("Andreas");
    }
    @Test
    void shouldReturnExistingProjectMembers() throws IOException, SQLException {
        HttpServer server = new HttpServer(10009, dataSource);
        ProjectMemberDao projectMemberDao = new ProjectMemberDao(dataSource);
        ProjectMember projectMember = new ProjectMember();
        projectMember.setFirstName("Ola");
        projectMember.setLastName("Nordmann");
        projectMember.setEmail("olanordmann@gmailcom");
        projectMember.setId(1);
        projectMemberDao.insert(projectMember);
        HttpClient client = new HttpClient("localhost", 10009, "/projectMember");
        assertThat(client.getResponseBody()).contains("<ul><li>Ola Nordmann - olanordmann@gmailcom </li></ul>");
    }

    @Test
    void shouldPostNewProjectTask() throws IOException, SQLException {
        HttpServer server = new HttpServer(10010, dataSource);
        String requestBody = "taskName=clean";
        HttpClient postClient = new HttpClient("localhost", 10010, "/newTask", "POST", requestBody);
        assertEquals(200, postClient.getStatusCode());

        HttpClient getClient = new HttpClient("localhost", 10010, "/tasks");
        assertThat(getClient.getResponseBody()).contains("<li>clean</li>");
    }

}