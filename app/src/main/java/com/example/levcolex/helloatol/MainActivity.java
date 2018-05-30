package com.example.levcolex.helloatol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.os.Bundle;
import android.widget.TextView;

import com.atol.drivers.fptr.Fptr;
import com.atol.drivers.fptr.IFptr;
import com.atol.drivers.fptr.settings.SettingsActivity;

import com.example.levcolex.helloatol.api.FiscalDriverImpl;
import com.example.levcolex.helloatol.api.FiscalDriver;

import com.example.levcolex.helloatol.model.Order;
import com.example.levcolex.helloatol.model.Printable;

import java.math.BigDecimal;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SHOW_SETTINGS = 1;
    private SharedPreferences preferences;
    private static final String FPTR_PREFERENCES = "FPTR_PREFERENCES";

    @NonNull
    private TextView infoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(FPTR_PREFERENCES, Context.MODE_PRIVATE);

        setContentView(R.layout.activity_main);
        infoTextView = findViewById(R.id.textView);

    }


    // обработчик кнопки "Настройки" -> запуск SettingsActivity.DEVICE_SETTINGS
    public void onOptions(View view) {
        ((TextView) findViewById(R.id.textView)).setText("Настройка драйвера");
        Intent intent = new Intent(this, SettingsActivity.class);
        String settings = getSettings();
        if (settings == null) {
            settings = getDefaultSettings();
        }
        intent.putExtra(SettingsActivity.DEVICE_SETTINGS, settings);
        startActivityForResult(intent, REQUEST_SHOW_SETTINGS);
    }


    public void onTest(View view) {


        IFptr fptr = new Fptr();
        try {
            fptr.create(getApplication()); // для create требуется контекст, пока не нашел как его получить
                                           // для класса не производного от контекста...
        }
        catch (Exception e) {
            infoTextView.setText(e.toString());
            return;
        }


//      TODO Implement FiscalDriverImpl interface and test the code below
        TestFiscalDriver driver = new TestFiscalDriver(new FiscalDriverImpl(fptr, getSettings())); // наверное, логично настройки передать в конструктор?

        try {
            driver.test();
        } catch (Exception e) {
            infoTextView.setText(e.toString());
        }
        finally {
            fptr.destroy();
        }


        /*
        ((TextView) findViewById(R.id.textView)).setText("Проверка...");
        IFptr fptr = new Fptr();
        try {
            fptr.create(getApplication());
            ((TextView) findViewById(R.id.textView)).setText(
                    ((TextView) findViewById(R.id.textView)).getText() +
                            "\nУстановка параметров драйвера..."

            );
            if (fptr.put_DeviceSettings(getSettings()) < 0) {
                checkError(fptr);
            }
            ((TextView) findViewById(R.id.textView)).setText(
                    ((TextView) findViewById(R.id.textView)).getText() +
                            "Ок");

            ((TextView) findViewById(R.id.textView)).setText(
                    ((TextView) findViewById(R.id.textView)).getText() +
                            "\nУстановка соединения...");

            if (fptr.put_DeviceEnabled(true) < 0) {
                checkError(fptr);
            }

            ((TextView) findViewById(R.id.textView)).setText(
                    ((TextView) findViewById(R.id.textView)).getText() +
                            "Ок");

            ((TextView) findViewById(R.id.textView)).setText(
                    ((TextView) findViewById(R.id.textView)).getText() +
                            "\nПроверка связи...");
            if (fptr.GetStatus() < 0) { // читаем данные из устройства
                checkError(fptr);
            }

            ((TextView) findViewById(R.id.textView)).setText(
                    ((TextView) findViewById(R.id.textView)).getText() +
                            "Ок");


            // можно получить доступ к данным (get_Date(), get_Time() и тд)
            ((TextView) findViewById(R.id.textView)).setText(
                    ((TextView) findViewById(R.id.textView)).getText() +
                            "\n" + fptr.get_Date().toString() + "   " + fptr.get_Time().toString());


            fptr.put_Caption("Print test...");
            fptr.put_Alignment(0);            // Alignment::AlignmentLeft
            fptr.put_TextWrap(0);             // TextWrap::TextWrapNone
            fptr.PrintString();

        } catch (Exception e) {
            ((TextView) findViewById(R.id.textView)).setText(e.toString());

        } finally {
            fptr.destroy();
        }

        */
    }

    private void printText(IFptr fptr, String s, int align, int wwrap) {
        fptr.put_Caption(s);
        fptr.put_Alignment(align);            // Alignment::AlignmentLeft
        fptr.put_TextWrap(wwrap);             // TextWrap::TextWrapNone
        fptr.PrintString();
    }


    private void AppendPos(IFptr fptr, String name, double price, double quantity, double positionSum) throws Exception {
        // добавление позиции в чек
        if (fptr.put_TaxNumber(IFptr.TAX_VAT_18) < 0) { // Устанавливает номер налога.
            checkError(fptr);
        }
        if (fptr.put_PositionSum(price * quantity) < 0) { // Устанавливает сумму позиции
            checkError(fptr);
        }
        if (fptr.put_Quantity(quantity) < 0) { // Устанавливает количество
            checkError(fptr);
        }
        if (fptr.put_Price(price) < 0) {  // Устанавливает цену
            checkError(fptr);
        }
        if (fptr.put_TextWrap(IFptr.WRAP_WORD) < 0) { // перенос слов
            checkError(fptr);
        }
        if (fptr.put_Name(name) < 0) { // наименование позиции
            checkError(fptr);
        }
        if (fptr.Registration() < 0) { // Производит регистрацию продажи / прихода.
            checkError(fptr);
        }
    }

    public void onPrintCheck(View view) {

        IFptr fptr = new Fptr();
        try {

            // 1. создание и инициализация
            fptr.create(getApplication());
            ((TextView) findViewById(R.id.textView)).setText(
                    ((TextView) findViewById(R.id.textView)).getText() +
                            "\nПечать чека..."

            );
            if (fptr.put_DeviceSettings(getSettings()) < 0) {
                checkError(fptr);
            }
            if (fptr.put_DeviceEnabled(true) < 0) {
                checkError(fptr);
            }
            if (fptr.GetStatus() < 0) { // читаем данные из устройства
                checkError(fptr);
            }

            // Аннулируем чек. (непонятно зачем, возможно чтобы его можно было перепечатать
            // в случае ошибки)
            try {
                if (fptr.CancelCheck() < 0) {
                    checkError(fptr);
                }
            } catch (Exception e) {
                int rc = fptr.get_ResultCode();
                if (rc != -16 && rc != -3801) {
                    throw e;
                }
            }

            try {

                if (fptr.put_Mode(IFptr.MODE_REGISTRATION) < 0) { // Устанаваливает значение режима работы. { ? }
                    checkError(fptr);
                }
                if (fptr.SetMode() < 0) { // Устанавливает режим ККТ.
                    // Выполняет вход в режим {put_Mode()} с паролем UserPassword.
                    checkError(fptr);
                }
                if (fptr.put_CheckType(IFptr.CHEQUE_TYPE_SELL) < 0) { // Устанавливает тип чека.
                    checkError(fptr);
                }
                if (fptr.OpenCheck() < 0) { // Открывает чек в ККТ
                    checkError(fptr);
                }


            } catch (Exception e) {
                // Проверка на превышение смены {?}
                if (fptr.get_ResultCode() == -3822) {

                    if (fptr.put_Mode(IFptr.MODE_REPORT_CLEAR) < 0) {
                        checkError(fptr);
                    }
                    if (fptr.SetMode() < 0) {
                        checkError(fptr);
                    }
                    if (fptr.put_ReportType(IFptr.REPORT_Z) < 0) {
                        checkError(fptr);
                    }
                    if (fptr.Report() < 0) {
                        checkError(fptr);
                    }

                    // еще раз открытие чека
                    if (fptr.put_Mode(IFptr.MODE_REGISTRATION) < 0) { // Устанаваливает значение режима работы. { ? }
                        checkError(fptr);
                    }
                    if (fptr.SetMode() < 0) { // Устанавливает режим ККТ.
                        // Выполняет вход в режим {put_Mode()} с паролем UserPassword.
                        checkError(fptr);
                    }
                    if (fptr.put_CheckType(IFptr.CHEQUE_TYPE_SELL) < 0) { // Устанавливает тип чека.
                        checkError(fptr);
                    }
                    if (fptr.OpenCheck() < 0) { // Открывает чек в ККТ
                        checkError(fptr);
                    }
                } else {
                    throw e;
                }
            }


            double price = 122, quantity = 1;
            BigDecimal sum = new BigDecimal(0);

            // AppendPos(IFptr fptr, String name, double price, double quantity, double positionSum
            AppendPos(fptr, "Кефир", price, quantity, price * quantity);
            sum = sum.add(new BigDecimal(price).multiply(new BigDecimal(quantity)));

            price = 44;
            quantity = 4;
            AppendPos(fptr, "Снежок", price, quantity, price * quantity);
            sum = sum.add(new BigDecimal(price).multiply(new BigDecimal(quantity)));

            // Оплата
            if (fptr.put_Summ(sum.doubleValue()) < 0) {
                checkError(fptr);
            }
            if (fptr.put_TypeClose(0) < 0) {
                checkError(fptr);
            }
            if (fptr.Payment() < 0) {
                checkError(fptr);
            }

            // Закрываем чек
            if (fptr.put_TypeClose(0) < 0) {
                checkError(fptr);
            }
            if (fptr.CloseCheck() < 0) {
                checkError(fptr);
            }

        } catch (Exception e) {
            ((TextView) findViewById(R.id.textView)).setText(e.toString());

        } finally {
            fptr.destroy();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SHOW_SETTINGS) { // обработчик завершения SettingsActivity.DEVICE_SETTINGS
            if (resultCode == RESULT_OK) {
                String settings = data.getExtras().getString(SettingsActivity.DEVICE_SETTINGS);
                ((TextView) findViewById(R.id.textView)).setText(settings);
                setSettings(settings);
            } else {
                ((TextView) findViewById(R.id.textView)).setText("Ошибка");
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void checkError(IFptr fptr) throws Exception {
        int rc = fptr.get_ResultCode(); // Возвращает результат последней операции (см. dto_errors.h)
        if (rc < 0) {
            String rd = fptr.get_ResultDescription(), // Возвращает текстовое описание результата последней операции
                    bpd = null;
            if (rc == -6) {
                bpd = fptr.get_BadParamDescription(); // Возвращает текстовое описание неверного параметра
            }
            if (bpd != null) {
                throw new Exception(String.format("[%d] %s (%s)", rc, rd, bpd));
            } else {
                throw new Exception(String.format("[%d] %s", rc, rd));
            }
        }
    }

    private String getSettings() {
        return preferences.getString(SettingsActivity.DEVICE_SETTINGS, getDefaultSettings());
    }

    private String getDefaultSettings() {
        IFptr fprint = new Fptr();
        fprint.create(this);
        String settings = fprint.get_DeviceSettings();
        fprint.destroy();
        return settings;
    }

    private void setSettings(String settings) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SettingsActivity.DEVICE_SETTINGS, settings);
        editor.apply();
    }
}
