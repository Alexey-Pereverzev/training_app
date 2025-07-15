package org.example.trainingapp.constant;

public enum Constant {
    ROLE("role"),
    BEARER("Bearer ");

    private final String value;

    Constant(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
