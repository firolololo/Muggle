package com.stellar.muggle.list;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author firo
 * @version 1.0
 * @date 2020/12/4 15:18
 */
public interface MuggleIterator<T> {
    boolean hasNext();
    T next();
    T remove();
    default void forEachRemaining(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        while (hasNext()) {
            action.accept(next());
        }
    }
}
