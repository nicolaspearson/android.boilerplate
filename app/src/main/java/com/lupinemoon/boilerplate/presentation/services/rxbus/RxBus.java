package com.lupinemoon.boilerplate.presentation.services.rxbus;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

public class RxBus<T> {
    
    private final Subject<T> subject;

    private static RxBus<Object> rxBusDefault;
    private static RxBus<Object> rxBusEmitLatest;

    public static synchronized RxBus<Object> getDefault() {
        if (rxBusDefault == null) {
            rxBusDefault = RxBus.createDefault();
        }

        return rxBusDefault;
    }

    public static synchronized RxBus<Object> getEmitLatest() {
        if (rxBusEmitLatest == null) {
            rxBusEmitLatest = RxBus.createEmitLatest();
        }

        return rxBusEmitLatest;
    }

    private RxBus() {
        this(PublishSubject.<T>create());
    }

    private RxBus(Subject<T> subject) {
        this.subject = subject;
    }

    public <E extends T> void post(E event) {
        subject.onNext(event);
    }

    public Observable<T> observe() {
        return subject;
    }

    public <E extends T> Observable<E> observeEvents(Class<E> eventClass) {
        // Pass only events of specified type, filter all other
        return subject.ofType(eventClass);
    }

    /**
     * PublishSubject emits to an observer only those items that are emitted by the
     * source Observable(s) subsequent to the time of the subscription.
     * See http://reactivex.io/documentation/subject.html
     */
    private static <T> RxBus<T> createDefault() {
        // PublishSubject is created in the constructor
        return new RxBus<>();
    }

    /**
     * When an observer subscribes to a BehaviorSubject, it begins by emitting the item
     * most recently emitted by the source Observable (or a seed/default value if none
     * has yet been emitted) and then continues to emit any other items emitted later
     * by the source Observable(s).
     * See http://www.introtorx.com/Content/v1.0.10621.0/02_KeyTypes.html#BehaviorSubject
     */
    private static <T> RxBus<T> createEmitLatest() {
        return new RxBus<>(BehaviorSubject.<T>create());
    }
    /**
     * ReplaySubject emits to any observer all of the items that were emitted by the
     * source Observable(s), regardless of when the observer subscribes.
     * See http://www.introtorx.com/Content/v1.0.10621.0/02_KeyTypes.html#ReplaySubject
     */
    private static <T> RxBus<T> createReplay(int numberOfEventsToReplay) {
        return new RxBus<>(ReplaySubject.<T>createWithSize(numberOfEventsToReplay));
    }
}