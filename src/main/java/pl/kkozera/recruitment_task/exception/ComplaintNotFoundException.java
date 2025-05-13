package pl.kkozera.recruitment_task.exception;

public class ComplaintNotFoundException extends RuntimeException {
    public ComplaintNotFoundException(String message) {
        super(message);
    }
}