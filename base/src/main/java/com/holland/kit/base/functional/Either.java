package com.holland.kit.base.functional;

import com.holland.kit.base.log.ILog;
import com.holland.kit.base.log.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Either<E, T> {
    public final E e;
    public final T t;

    public Either(E e, T t) {
        this.e = e;
        this.t = t;
    }

    public static <E, T> Either<E, T> success(T t) {
        return new Either<>(null, t);
    }

    public static <E, T> Either<E, T> error(E e) {
        return new Either<>(e, null);
    }

    // todo ThreadLocal to deal chains
    public <V> Either<E, V> then(Function<T, Either<E, V>> after) {
        if (e != null) {
            return new Either<>(e, null);
        } else {
            return after.apply(t);
        }
    }

    public Either<E, T> peek(Consumer<T> after) {
        after.accept(this.t);
        return this;
    }

    public void end(Consumer<E> onError, Consumer<T> onSuccess) {
        if (e != null) {
            onError.accept(e);
        } else {
            onSuccess.accept(t);
        }
    }

//    public static void main(String[] args) {
//        ILog    log = LogFactory.create(Either.class);
//        Integer s   = 1;
//
//        Either<String, String> either = t1(s)
//                .then(Either::t2)
//                .then(str -> new Either<>(null, str + "(" + s + ")"));
////                .then(Either::t3)
////                .then(Either::t4);
//        log.error(either.e);
////        log.info(String.join(",", either.t));
//        log.info(either.t);
//    }


//    public static Either<String, String> t1(Integer s) {
//        return new Either<>(null, s + "t1");
//    }
//
//    public static Either<String, String> t2(String s) {
//        return new Either<>("null1", s + "t2");
//    }
//
//    public static Either<String, String> t3(String s) {
//        return new Either<>(null, s + "t3");
//    }
//
//    public static Either<String, List<String>> t4(String s) {
//        ArrayList<String> objects = new ArrayList<>();
//        objects.add("_");
//        objects.add(s);
//        return new Either<>(null, objects);
//    }
}
