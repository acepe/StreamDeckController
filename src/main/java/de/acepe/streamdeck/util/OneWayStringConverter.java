package de.acepe.streamdeck.util;

import javafx.util.StringConverter;

import java.util.function.Function;

public class OneWayStringConverter<T> extends StringConverter<T> {
    private final Function<T, String> toStringFunction;

    public OneWayStringConverter(Function<T, String> toStringFunction) {
        this.toStringFunction = toStringFunction;
    }

    @Override
    public String toString(T object) {
        return object == null ? null : toStringFunction.apply(object);
    }

    @Override
    public T fromString(String string) {
        return null;
    }
}
