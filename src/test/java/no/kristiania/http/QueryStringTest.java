package no.kristiania.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryStringTest {

    @Test
    void shouldRetrieveQueryParameter() {
        QueryString querystring = new QueryString("status=200");
        assertEquals("200", querystring.getParameter("status"));
    }

    @Test
    void shouldRetrieveOtherQueryParameter() {
        QueryString querystring = new QueryString("status=404");
        assertEquals("404", querystring.getParameter("status"));
    }

    @Test
    void shouldRetrieveParameterByName() {
        QueryString queryString = new QueryString("text=Hello");
        assertEquals(null, queryString.getParameter("status"));
        assertEquals("Hello", queryString.getParameter("text"));

    }

    @Test
    void shouldHandleMultipleParameters() {
        QueryString queryString = new QueryString("text=Hello&status=200");
        assertEquals("200", queryString.getParameter("status"));
        assertEquals("Hello", queryString.getParameter("text"));
    }

    @Test
    void shouldParseSeveralParameters() {
        QueryString queryString = new QueryString("status=200&body=Hello");
        assertEquals("200", queryString.getParameter("status"));
        assertEquals("Hello", queryString.getParameter("body"));
    }
    @Test
    void shouldSerializeQueryString() {
        QueryString queryString = new QueryString("status=200");
        assertEquals("?status=200", queryString.getQueryString());
        queryString.addParameter("body", "Hello");
        assertEquals("?status=200&body=Hello", queryString.getQueryString());
    }
}




