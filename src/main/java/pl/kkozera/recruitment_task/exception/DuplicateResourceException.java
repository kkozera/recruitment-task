package pl.kkozera.recruitment_task.exception;

public class DuplicateResourceException extends IllegalArgumentException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
