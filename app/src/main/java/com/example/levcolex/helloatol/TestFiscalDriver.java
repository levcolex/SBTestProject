package com.example.levcolex.helloatol;

import android.support.annotation.NonNull;

import com.example.levcolex.helloatol.api.FiscalDriver;
import com.example.levcolex.helloatol.model.Order;
import com.example.levcolex.helloatol.model.Position;
import com.example.levcolex.helloatol.model.Printable;
import com.example.levcolex.helloatol.model.SimpleString;
import com.example.levcolex.helloatol.model.consts.Alignment;
import com.example.levcolex.helloatol.model.consts.Tax;
import com.example.levcolex.helloatol.model.consts.Wrapping;

import java.math.BigDecimal;
import java.util.ArrayList;
    import java.util.List;

public class TestFiscalDriver {

    @NonNull
    private FiscalDriver driver;

    private Order order;

    public TestFiscalDriver(@NonNull FiscalDriver driver) {
        this.driver = driver;
        initOrder();
    }

    private void initOrder() {
        Order order = new Order();

        // HEADER
        List<Printable> header = new ArrayList<>();
        header.add(new SimpleString("Заказ № 1234", Alignment.CENTER, Wrapping.WORD));
        header.add(new SimpleString("Официант: Руженцов А.", Alignment.LEFT, Wrapping.WORD));
        header.add(new SimpleString("Посадочное место: Летняя веранда стол 1",
                Alignment.RIGHT, Wrapping.WORD));
        header.add(new SimpleString("===========", Alignment.CENTER, Wrapping.WORD));
        order.header = header;

        // POSITIONS
        Tax[] taxes = Tax.values();
        List<Position> positions = new ArrayList<>();
        for (int index = 1; index < 5; index++) {
            Position position = new Position();
            position.name = "Блюдо " + index;
            position.price = new BigDecimal(index).multiply(BigDecimal.TEN);
            position.quantity = new BigDecimal(index);
            position.total = position.price.multiply(position.quantity);
            position.tax = taxes[index % taxes.length];
            positions.add(position);
        }
        order.positions = positions;

        // FOOTER
        List<Printable> footer = new ArrayList<>();
        header.add(new SimpleString("===========", Alignment.CENTER, Wrapping.WORD));
        header.add(new SimpleString("Бизнес-ланч всего за 175р. с 12:00 до 15:00 по будням",
                Alignment.LEFT, Wrapping.WORD));
        header.add(new SimpleString("Мы работаем с 10:00 до 23:00", Alignment.CENTER, Wrapping.WORD));
        order.footer = footer;
    }

    public void test() throws RuntimeException {
        driver.prepare();
        driver.printZReport();
        driver.sale(order);
        driver.refund(order);
        driver.finish();
    }

}
