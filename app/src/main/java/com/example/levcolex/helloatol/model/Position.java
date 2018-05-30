package com.example.levcolex.helloatol.model;

import android.support.annotation.NonNull;

import com.example.levcolex.helloatol.model.consts.Tax;

import java.math.BigDecimal;

public class Position {

    // наименование позиции
    @NonNull
    public String name = "";

    // количество
    @NonNull
    public BigDecimal quantity = BigDecimal.ZERO;

    // цена позиции
    @NonNull
    public BigDecimal price = BigDecimal.ZERO;

    // зачем это поле?
    @NonNull
    public BigDecimal total = BigDecimal.ZERO;


    @NonNull
    public Tax tax = Tax.NO_VAT;

}
