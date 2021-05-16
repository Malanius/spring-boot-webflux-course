package cz.malanius.webflux;

import lombok.Data;

@Data
public class CustomException extends Throwable {

    private String message;

    public CustomException(Throwable e) {
        this.message = e.getMessage();
    }
}
