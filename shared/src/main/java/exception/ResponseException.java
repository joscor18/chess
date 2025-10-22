package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {

    private final int StatusCode;

    public ResponseException(String message, int StatusCode) {
        super(message);
        this.StatusCode = StatusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", StatusCode));
    }

    public int toHttpStatusCode() {
        return StatusCode;
    }
}