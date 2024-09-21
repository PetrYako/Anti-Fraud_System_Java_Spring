package antifraud.model;

public enum UserStatus {
    LOCKED("LOCK"), UNLOCKED("UNLOCK");

    private final String operation;

    UserStatus(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return operation;
    }
}
