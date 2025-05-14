package com.castle.property.application.config.exception.dto;

import com.castle.property.application.config.exception.handler.Translator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    // HTTP status questionCode
    private HttpStatus status;

    private int statusCode;

    //List of constructed messages
    private List<String> messages;

    public ErrorResponse() {
    }

    public ErrorResponse(HttpStatus status, String message) {
        //change http status to 403
        if (message.equalsIgnoreCase("Access is denied")) {
            status = HttpStatus.FORBIDDEN;
        }
        this.status = status;
        this.statusCode = status.value();
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        if (message.contains(".")) {
            this.messages.add(Translator.toLocale(message, null));
        } else {
            switch (message) {
                case "Access is denied":
                    this.messages.add(Translator.toLocale("oauth.access.denied", null));
                    break;
                case "User is disabled":
                    this.messages.add(Translator.toLocale("oauth.access.account.disabled", null));
                    break;
                case "Access token expired":
                    this.messages.add(Translator.toLocale("oauth.access.token.expired", null));
                    break;
                case "Bad credentials":
                    this.messages.add(Translator.toLocale("oauth.access.bad.credentials", null));
                    break;
                case "Full authentication is required to access this resource":
                    this.messages.add(Translator.toLocale("oauth.authentication.required", null));
                    break;
                default:
                    this.messages.add(message);
                    break;
            }
        }
    }

    public ErrorResponse(HttpStatus status, List<String> messages) {
        this.status = status;
        this.statusCode = status.value();
        this.messages = messages;
    }

}
