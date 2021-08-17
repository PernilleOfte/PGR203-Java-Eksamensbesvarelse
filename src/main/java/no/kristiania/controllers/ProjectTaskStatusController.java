package no.kristiania.controllers;

import no.kristiania.http.HttpController;
import no.kristiania.http.HttpMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ProjectTaskStatusController implements HttpController {
    public static final Map<Integer, String> statuses = new HashMap<>() {{
        put(1, "To do");
        put(2, "In progress");
        put(3, "Complete");
    }};

    @Override
    public void handle(HttpMessage request, Socket clientSocket) throws IOException {
        HttpMessage response = new HttpMessage(getBody());
        response.write(clientSocket);
    }

    public String getBody() {
        String body = "";
        for (Map.Entry<Integer, String> status : statuses.entrySet()) {
            body += "<option value=" + status.getKey() + ">" + status.getValue() + "</option>";
        }

        return body;
    }
}