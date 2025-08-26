package org.example.trainingapp.filter;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import jakarta.servlet.FilterChain;
import java.nio.charset.StandardCharsets;
import nl.altindag.log.LogCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RestLoggingFilterTest {

    private RestLoggingFilter filter;
    private LogCaptor logCaptor;

    @BeforeEach
    void setUp() {
        filter = new RestLoggingFilter();
        logCaptor = LogCaptor.forClass(RestLoggingFilter.class);
    }


    @Test
    void whenDoFilterInternal_normalRequest_shouldLogInfoWithBodies() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/hello");
        request.setContent("req-body".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setContentType("text/plain");
        FilterChain chain = (req, res) -> res.getWriter().write("resp-body");
        // when
        filter.doFilterInternal(request, response, chain);
        // then
        assertThat(logCaptor.getInfoLogs())
                .anyMatch(m -> m.contains("REST GET /api/hello"))
                .anyMatch(m -> m.contains("REQ:<empty>"))  // тело не читается, значит пусто
                .anyMatch(m -> m.contains("RES:resp-body"));
        assertEquals("resp-body", response.getContentAsString());
    }


    @Test
    void whenDoFilterInternal_sensitiveEndpoint_shouldHideBodies() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/users/login");
        request.setContent("secret".getBytes(StandardCharsets.UTF_8));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> res.getWriter().write("secret-response");
        // when
        filter.doFilterInternal(request, response, chain);
        // then
        assertTrue(logCaptor.getInfoLogs().stream().anyMatch(log -> log.contains("<hidden>")));
        assertEquals("secret-response", response.getContentAsString());
    }


    @Test
    void whenDoFilterInternal_errorStatus_shouldLogWarn() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/error");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> {
            HttpServletResponse httpRes = (HttpServletResponse) res; // правильный каст
            httpRes.setStatus(500);
            httpRes.getWriter().write("fail");
        };
        // when
        filter.doFilterInternal(request, response, chain);
        // then
        assertThat(logCaptor.getWarnLogs())
                .anyMatch(m -> m.contains("REST GET /api/error"))
                .anyMatch(m -> m.contains("fail"));
        assertEquals("fail", response.getContentAsString());
    }


    @Test
    void whenDoFilterInternal_emptyBody_shouldLogEmptyPlaceholder() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> {                 // put nothing
        };
        // when
        filter.doFilterInternal(request, response, chain);
        // then
        assertThat(logCaptor.getInfoLogs()).anyMatch(m -> m.contains("<empty>"));
        assertEquals("", response.getContentAsString());
    }
}
