package com.example.levcolex.helloatol.api;

import android.support.annotation.NonNull;

// import com.example.levcolex.helloatol.model.consts.Alignment;
// import com.example.levcolex.helloatol.model.consts.Wrapping;

import com.example.levcolex.helloatol.model.Order;
import com.example.levcolex.helloatol.model.Printable;

// import java.util.Collections;
import java.util.List;

 // test() ->
 //       driver.prepare();
 //       driver.printZReport();
 //       driver.sale(order);
 //       driver.refund(order);


public class FiscalDriverImpl implements FiscalDriver {
    @NonNull
    private FiscalDriver fptr;
    //
    @Override
    public void prepare()
    {

    }

    //
    @Override
    public  void sale(Order order)
    {

    }

    //
    @Override
    public void refund(Order order)
    {

    }

    @Override
    public void print(List<Printable> printData)
    {

    }

    @Override
    public void printZReport()
    {

    }

    @Override
    public void finish()
    {

    }

}
