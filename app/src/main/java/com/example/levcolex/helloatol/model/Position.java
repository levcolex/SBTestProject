package com.example.levcolex.helloatol.model;

import android.support.annotation.NonNull;

import com.example.levcolex.helloatol.model.consts.Tax;

import java.math.BigDecimal;

public class Position {

    @NonNull
    public String name = "";

    @NonNull
    public BigDecimal quantity = BigDecimal.ZERO;

    @NonNull
    public BigDecimal price = BigDecimal.ZERO;

    @NonNull
    public BigDecimal total = BigDecimal.ZERO;

    @NonNull
    public Tax tax = Tax.NO_VAT;

}
