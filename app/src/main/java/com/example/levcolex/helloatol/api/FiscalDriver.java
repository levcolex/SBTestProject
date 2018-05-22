package com.example.levcolex.helloatol.api;

import com.example.levcolex.helloatol.model.Order;
import com.example.levcolex.helloatol.model.Printable;

import java.util.List;

public interface FiscalDriver {

    void prepare();

    void sale(Order order);

    void refund(Order order);

    void print(List<Printable> printData);

    void printZReport();

    void finish();

}
