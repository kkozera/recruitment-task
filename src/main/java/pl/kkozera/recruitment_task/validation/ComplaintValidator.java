package pl.kkozera.recruitment_task.validation;

public class ComplaintValidator {

    public static void validateSortField(String sortBy) {
        try {
            ComplaintSortField.valueOf(sortBy);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid sort field: " + sortBy);
        }
    }
}
