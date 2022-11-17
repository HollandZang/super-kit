package com.holland.kit.base.asm;

public interface DeepClone<T> {
    default T deepClone() {
        return null;
    }
}
