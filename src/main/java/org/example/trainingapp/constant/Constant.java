package org.example.trainingapp.constant;

import lombok.Getter;


@Getter
public enum Constant {
    ROLE("role"),
    BEARER("Bearer ");

    private final String value;

    Constant(String value) {
        this.value = value;
    }
}
