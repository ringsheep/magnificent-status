package org.ziniakov.magnificentstatus.service;

import org.springframework.stereotype.Service;

@Service
public class ObservedAppLogger {

    /**
     * an encapsulating logging method for testability/decoupling purposes
     * @param message text to log
     */
    public void log(String message) {
        System.out.println(message);
    }
}
