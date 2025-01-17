package com.tksimeji.wobject.event;

public interface Cancellable {
    boolean isCancelled();

    void setCancel(boolean cancel);
}
