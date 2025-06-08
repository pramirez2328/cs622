package service;

public class InvalidInputException extends RuntimeException {
    private final String input;

    public InvalidInputException(String message, String input) {
        super(message);
        this.input = input;
    }

    public String getInvalidInput() {
        return input;
    }
}
