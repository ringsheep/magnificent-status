package org.ziniakov.magnificentstatus.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.ziniakov.magnificentstatus.model.ObservedAppState;
import org.ziniakov.magnificentstatus.properties.ObservedAppProperties;

@Service
@RequiredArgsConstructor
public class ObservedAppStateChecker {

    private final RestTemplate restTemplate;
    private final ObservedAppProperties appProperties;
    private final ObservedAppLogger logger;

    @Getter
    private volatile ObservedAppState state = ObservedAppState.HEALTHY;

    /**
     * checks health of observed app every healthCheckDelay milliseconds
     * writes to log only if state changes from previous
     */
    // TODO: find a way to move delay value to ConfigurationProperties
    @Scheduled(fixedDelayString = "${observed-app.healthCheckDelay}")
    public void checkState() {
        var previousStatus = state;
        state = fetchState();
        if (previousStatus != state) {
            logger.log(appProperties.getName() + " has changed it's health state to: " + state);
        }
    }

    /**
     * fetches http response code of observed url. if there is no valid http code - writes error log
     * @return state of observed app
     */
    private ObservedAppState fetchState() {
        try {
            var response = restTemplate.getForEntity(appProperties.getUrl(), String.class);
            return response.getStatusCode().is2xxSuccessful() ? ObservedAppState.HEALTHY : ObservedAppState.UNHEALTHY;
        } catch(HttpServerErrorException e) {
            return e.getStatusCode().isError() ? ObservedAppState.UNHEALTHY : ObservedAppState.UNAVAILABLE;
        } catch (Exception e) {
            logger.log("Exception during health checking: " + e.toString());
            return ObservedAppState.UNAVAILABLE;
        }
    }
}
