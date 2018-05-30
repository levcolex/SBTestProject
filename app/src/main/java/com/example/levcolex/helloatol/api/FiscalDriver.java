package com.example.levcolex.helloatol.api;

        import com.example.levcolex.helloatol.model.Order;
        import com.example.levcolex.helloatol.model.Printable;

        import java.util.List;


public interface FiscalDriver {

    // 1. подготовительные действия
    // 2. инициализация драйвера
    // 3. проверка связи
    void prepare();

    // собственно продажа
    void sale(Order order);

    // возврат товара (?)
    void refund(Order order);

    // предположим, эта функция должна просто печатать текст из printData
    void print(List<Printable> printData);

    //
    void printZReport();

    // что должна делать эта функция?
    void finish();
}
