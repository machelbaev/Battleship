package battleship.helpers;

public class Response {

    public enum ResponseStatus { SUCCESS, ERROR }

    private String message;

    private ResponseStatus status;

    Response(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public ResponseStatus getStatus() {
        return status;
    }
}
