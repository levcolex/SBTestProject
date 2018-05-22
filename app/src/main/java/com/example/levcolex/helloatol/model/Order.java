package com.example.levcolex.helloatol.model;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

public class Order {

    @NonNull
    public List<Printable> header = Collections.emptyList();

    @NonNull
    public List<Printable> footer = Collections.emptyList();

    @NonNull
    public List<Position> positions = Collections.emptyList();

}
