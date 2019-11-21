package org.ziniakov.magnificentstatus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ziniakov.magnificentstatus.service.ObservedAppStateChecker;

@RestController
@RequiredArgsConstructor
public class StateController {

    private final ObservedAppStateChecker checker;

    /**
     * @return current state of observed app (healty/unhealthy/unavaialbe)
     */
    @GetMapping(path = "/state")
    public String getState() {
        return checker.getAppAvailabilityStatus().toString();
    }
}
