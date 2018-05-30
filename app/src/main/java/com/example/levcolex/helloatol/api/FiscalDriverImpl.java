// !!->> - требует уточнения

package com.example.levcolex.helloatol.api;

import android.support.annotation.NonNull;

// import com.example.levcolex.helloatol.model.consts.Alignment;
// import com.example.levcolex.helloatol.model.consts.Wrapping;

import com.atol.drivers.fptr.IFptr;
//import com.atol.drivers.fptr.Fptr;

import com.example.levcolex.helloatol.model.Order;
import com.example.levcolex.helloatol.model.Position;
import com.example.levcolex.helloatol.model.Printable;

// import java.util.Collections;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

//import java.util.Iterator;
//import java.lang.Iterable;

 // test() ->
 //       driver.prepare();      +
 //       driver.printZReport(); +
 //       driver.sale(order);
 //       driver.refund(order);
 //       driver.finish();


public class FiscalDriverImpl implements FiscalDriver {
    @NonNull
    private IFptr fptr;

    @NonNull
    private String settings;


    public FiscalDriverImpl(@NonNull IFptr _fptr, @NonNull String _settings)
    {
        fptr = _fptr;
        settings = _settings;
    }
    //

    @Override
    public void prepare() throws FiscalDriverException
    {

        if (fptr.put_DeviceSettings(settings) < 0) {
            checkError();
        }

        if (fptr.put_DeviceEnabled(true) < 0) {
            checkError();
        }

        if (fptr.GetStatus() < 0) {
            checkError();
        }

        // Отменяем чек, если уже открыт. Ошибки "Неверный режим" и "Чек уже закрыт"
        // не являются ошибками, если мы хотим просто отменить чек
         try {
            if (fptr.CancelCheck() < 0) {
                checkError();
            }
        } catch (FiscalDriverException e) {
            int rc = fptr.get_ResultCode();
            if (rc != -16 && rc != -3801) {
                throw e;
            }
        }

    }

    //
    @Override
    public  void sale(Order order)
    {

        try {
            openCheck(IFptr.CHEQUE_TYPE_SELL);
        } catch (FiscalDriverException e) {
            // Проверка на превышение смены
            if (fptr.get_ResultCode() == -3822) {
                printZReport();
                openCheck(IFptr.CHEQUE_TYPE_SELL);
            } else {
                throw e;
            }
        }

        // печать HEADER - предположительно какая-то информация специфичная для конкретного документа
        this.print(order.header);
        BigDecimal Summ = BigDecimal.ZERO;

        for(Iterator<Position> i = order.positions.iterator(); i.hasNext();) {

            Position _pos = i.next();

            Summ = Summ.add(AppendPos(_pos));

            if (fptr.Registration() < 0) { // Производит регистрацию продажи / прихода.
                checkError();
            }

        }


        // Оплата
        if (fptr.put_Summ(Summ.doubleValue()) < 0) {
            checkError();
        }
        if (fptr.put_TypeClose(0) < 0) {
            checkError();
        }
        if (fptr.Payment() < 0) {
            checkError();
        }

        // печать FOOTER - предположительно какая-то информация специфичная для конкретного документа
        this.print(order.footer);


        closeCheck(0);
    }

    //
    @Override
    public void refund(Order order)
    {
        try {
            openCheck(IFptr.CHEQUE_TYPE_SELL);
        } catch (FiscalDriverException e) {
            // Проверка на превышение смены
            if (fptr.get_ResultCode() == -3822) {
                printZReport();
                openCheck(IFptr.CHEQUE_TYPE_SELL);
            } else {
                throw e;
            }
        }

        // печать HEADER - предположительно какая-то информация специфичная для конкретного документа
        this.print(order.header);
        BigDecimal Summ = BigDecimal.ZERO;

        for(Iterator<Position> i = order.positions.iterator(); i.hasNext();) {

            Position _pos = i.next();

            Summ = Summ.add(AppendPos(_pos));

            if (fptr.BuyReturn() < 0) { // Производит регистрацию продажи / прихода.
                checkError();
            }

        }

        // возврат ? - какие-то еще действия?

        // печать FOOTER - предположительно какая-то информация специфичная для конкретного документа
        this.print(order.footer);

        closeCheck(0);

    }

    @Override
    public void print(List<Printable> printData) throws FiscalDriverException   {
        for(Iterator<Printable> i = printData.iterator(); i.hasNext();)
        {
            Printable _item = i.next();
            if(fptr.put_Caption(_item.getPrintString()) < 0) {
                checkError();
            }
            if(fptr.put_Alignment(_item.getAlignment().ordinal()) < 0) {
                checkError();
            }
            if(fptr.put_TextWrap(_item.getWrapping().ordinal()) < 0) {
                checkError();
            }
            if(fptr.PrintString() < 0) {
                checkError();
            }
        }
    }

    @Override
    public void printZReport()
    {
        // установка режима отчетов с гашением
        if (fptr.put_Mode(IFptr.MODE_REPORT_CLEAR) < 0) {
            checkError();
        }
        if (fptr.SetMode() < 0) {
            checkError();
        }

        // Суточный отчет с гашением
        if (fptr.put_ReportType(IFptr.REPORT_Z) < 0) {
            checkError();
        }
        if (fptr.Report() < 0) {
            checkError();
        }
    }

    @Override
    public void finish()
    {

    }


    private BigDecimal AppendPos(Position pos) throws FiscalDriverException {
            // добавление позиции в чек
            if (fptr.put_TaxNumber(pos.tax.ordinal()) < 0) { // Устанавливает номер налога.
                checkError();
            }
            if (fptr.put_PositionSum( pos.price.multiply(pos.quantity).doubleValue() ) < 0) { // Устанавливает сумму позиции
                checkError();
            }
            if (fptr.put_Quantity(pos.quantity.doubleValue()) < 0) { // Устанавливает количество
                checkError();
            }
            if (fptr.put_Price(pos.price.doubleValue()) < 0) {  // Устанавливает цену
                checkError();
            }
            if (fptr.put_TextWrap(IFptr.WRAP_WORD) < 0) { // перенос слов
                checkError();
            }
            if (fptr.put_Name(pos.name) < 0) { // наименование позиции
                checkError();
            }

            return pos.price.multiply(pos.quantity); // !!->> проверить
    }


    private void closeCheck(int typeClose) throws FiscalDriverException {
        if (fptr.put_TypeClose(typeClose) < 0) {
            checkError();
        }
        if (fptr.CloseCheck() < 0) {
            checkError();
        }
    }

    private void openCheck(int type) throws FiscalDriverException {
        if (fptr.put_Mode(IFptr.MODE_REGISTRATION) < 0) { // Устанаваливает значение режима работы {?}
            checkError();
        }
        if (fptr.SetMode() < 0) {
            checkError();
        }
        if (fptr.put_CheckType(type) < 0) {
            checkError();
        }
        if (fptr.OpenCheck() < 0) {
            checkError();
        }
    }

    private void checkError() throws FiscalDriverException {
        int rc = fptr.get_ResultCode();
        if (rc < 0) {
            String rd = fptr.get_ResultDescription(), bpd = null;
            if (rc == -6) {
                bpd = fptr.get_BadParamDescription();
            }
            if (bpd != null) {
                throw new FiscalDriverException(String.format("[%d] %s (%s)", rc, rd, bpd));
            } else {
                throw new FiscalDriverException(String.format("[%d] %s", rc, rd));
            }
        }
    }

    private static class FiscalDriverException extends RuntimeException {
        public FiscalDriverException(String msg) {
            super(msg);
        }

    }

}
