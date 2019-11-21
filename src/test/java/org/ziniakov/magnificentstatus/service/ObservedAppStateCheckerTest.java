package org.ziniakov.magnificentstatus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.ziniakov.magnificentstatus.model.ObservedAppState;
import org.ziniakov.magnificentstatus.properties.ObservedAppProperties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class ObservedAppStateCheckerTest {

    private RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private ObservedAppProperties appProperties = new ObservedAppProperties()
            .setName("testService")
            .setUrl("http://example.com");
    private ObservedAppLogger logger = Mockito.mock(ObservedAppLogger.class);

    private ObservedAppStateChecker checker;

    @BeforeEach
    void setUp() {
        checker = new ObservedAppStateChecker(restTemplate, appProperties, logger);
    }

    @Test
    void checkState_should_log_on_new_state() {
        throwsHttpException();

        checker.checkState();

        verify(logger).log("testService has changed it's health state to: UNHEALTHY");
    }

    @Test
    void getState_should_return_healthy_on_http_200() {
        doReturn(new ResponseEntity<String>(HttpStatus.OK))
                .when(restTemplate).getForEntity(appProperties.getUrl(), String.class);
        checker.checkState();

        var state = checker.getState();

        assertEquals(ObservedAppState.HEALTHY, state);
    }

    @Test
    void getState_should_return_unhealthy_on_http_exception() {
        throwsHttpException();
        checker.checkState();

        var state = checker.getState();

        assertEquals(ObservedAppState.UNHEALTHY, state);
    }

    @Test
    void getState_should_return_unavailable_on_any_other_exception() {
        throwsInvalidException();
        checker.checkState();

        var state = checker.getState();

        assertEquals(ObservedAppState.UNAVAILABLE, state);
    }

    @Test
    void getState_should_log_on_any_other_exception() {
        throwsInvalidException();

        checker.checkState();

        verify(logger).log("Exception during health checking: java.lang.RuntimeException: something gone wrong :(");
    }

    private void throwsHttpException() {
        doThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(restTemplate).getForEntity(appProperties.getUrl(), String.class);
    }

    private void throwsInvalidException() {
        doThrow(new RuntimeException("something gone wrong :("))
                .when(restTemplate).getForEntity(appProperties.getUrl(), String.class);
    }
}