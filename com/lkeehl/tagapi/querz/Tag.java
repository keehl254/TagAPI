package com.lkeehl.tagapi.querz;

import java.util.Objects;

public abstract class Tag<T> {

    private final T value;

    public Tag(T value) {
        this.value = Objects.requireNonNull(value);
    }

    protected T getValue() {
        return value;
    }


}
