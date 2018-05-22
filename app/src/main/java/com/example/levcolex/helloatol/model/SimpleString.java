package com.example.levcolex.helloatol.model;

import android.support.annotation.NonNull;

import com.example.levcolex.helloatol.model.consts.Alignment;
import com.example.levcolex.helloatol.model.consts.Wrapping;

public class SimpleString implements Printable {

    @NonNull
    private final String data;

    @NonNull
    private final Alignment alignment;

    @NonNull
    private final Wrapping wrapping;

    public SimpleString(@NonNull String data,
                        @NonNull Alignment alignment,
                        @NonNull Wrapping wrapping) {
        this.data = data;
        this.alignment = alignment;
        this.wrapping = wrapping;
    }

    @NonNull
    @Override
    public String getPrintString() {
        return data;
    }

    @NonNull
    @Override
    public Alignment getAlignment() {
        return alignment;
    }

    @NonNull
    @Override
    public Wrapping getWrapping() {
        return wrapping;
    }
}
