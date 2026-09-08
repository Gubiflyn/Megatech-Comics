package com.megatech.bffservice.exception;

import org.springframework.http.HttpStatusCode;

/**
 * Wraps a failure that occurred while calling a downstream microservice.
 * "passthrough" distinguishes a business error the downstream service
 * itself returned (its status/body should be relayed to the client) from
 * a connectivity failure (timeout, connection refused, DNS), which is
 * always surfaced as 502 regardless of what status this exception carries.
 */
public class DownstreamServiceException extends RuntimeException {

    private final HttpStatusCode status;
    private final String body;
    private final boolean passthrough;

    public DownstreamServiceException(HttpStatusCode status, String body, boolean passthrough) {
        super("Downstream service call failed with status " + status);
        this.status = status;
        this.body = body;
        this.passthrough = passthrough;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public boolean isPassthrough() {
        return passthrough;
    }
}
