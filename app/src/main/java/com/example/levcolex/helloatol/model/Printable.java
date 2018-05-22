package com.example.levcolex.helloatol.model;

import android.support.annotation.NonNull;

import com.example.levcolex.helloatol.model.consts.Alignment;
import com.example.levcolex.helloatol.model.consts.Wrapping;

public interface Printable {

    @NonNull
    String getPrintString();

    @NonNull
    Alignment getAlignment();

    @NonNull
    Wrapping getWrapping();

}
