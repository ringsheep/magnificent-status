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

    @Getter
    private volatile ObservedAppState state = ObservedAppState.HEALTHY;

    /**
     * checks health of observed app every healthCheckDelay milliseconds
     * writes to log only if state changes from previous
     */
    @Scheduled(fixedDelayString = "${observed-app.healthCheckDelay}")
    public void checkStatus() {
        var previousStatus = state;
        state = getAppAvailabilityStatus();
        if (previousStatus != state) {
            System.out.println(appProperties.getName() + " has changed it's availability state to: " + state);
        }
    }

    /**
     * watches http response code of observed url. if there is no valid http code - writes error log
     * @return state of observed app
     */
    public ObservedAppState getAppAvailabilityStatus() {
        try {
            var response = restTemplate.getForEntity(appProperties.getUrl(), String.class);
            return response.getStatusCode().is2xxSuccessful() ? ObservedAppState.HEALTHY : ObservedAppState.UNHEALTHY;
        } catch(HttpServerErrorException e) {
            return e.getStatusCode().isError() ? ObservedAppState.UNHEALTHY : ObservedAppState.UNAVAILABLE;
        } catch (Exception e) {
            System.out.println("Exception during health checking: " + e.toString());
            return ObservedAppState.UNAVAILABLE;
        }
    }
}
