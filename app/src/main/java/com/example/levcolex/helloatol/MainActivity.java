package com.example.levcolex.helloatol;

        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.os.Bundle;
        import android.widget.TextView;

        import com.atol.drivers.fptr.Fptr;
        import com.atol.drivers.fptr.IFptr;
        import com.atol.drivers.fptr.settings.SettingsActivity;



public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SHOW_SETTINGS = 1;
    private SharedPreferences preferences;
    private static final String FPTR_PREFERENCES = "FPTR_PREFERENCES";

    //TextView myTextView = (TextView)findViewById(R.id.textView);

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(FPTR_PREFERENCES, Context.MODE_PRIVATE);

        setContentView(R.layout.activity_main);

    }


    // обработчик кнопки "Настройки" -> запуск SettingsActivity.DEVICE_SETTINGS
    public void onOptions(View view) {
        ((TextView)findViewById(R.id.textView)).setText("Настройка драйвера");
        Intent intent = new Intent(this, SettingsActivity.class);
        String settings = getSettings();
        if (settings == null) {
            settings = getDefaultSettings();
        }
        intent.putExtra(SettingsActivity.DEVICE_SETTINGS, settings);
        startActivityForResult(intent, REQUEST_SHOW_SETTINGS);
    }

    // обработчик кнопки "Настройки" последовательно выполняю
    // put_DeviceSettings - Устанавливает настройки драйвера
    // put_DeviceEnabled  - Происходит попытка установки связи с устройством
    // GetStatus          - Заполняет свойства драйвера текущим состоянием ККТ
    //
    public void onTest(View view) {

        ((TextView)findViewById(R.id.textView)).setText("Проверка...");
        IFptr fptr = new Fptr();
        try {
            fptr.create(getApplication());
            ((TextView)findViewById(R.id.textView)).setText(
                    ((TextView)findViewById(R.id.textView)).getText() +
                            "\nУстановка параметров драйвера..."

            );
            if (fptr.put_DeviceSettings(getSettings()) < 0) {
                checkError(fptr);
            }
            ((TextView)findViewById(R.id.textView)).setText(
                    ((TextView)findViewById(R.id.textView)).getText() +
                            "Ок");

            ((TextView)findViewById(R.id.textView)).setText(
                    ((TextView)findViewById(R.id.textView)).getText() +
                            "\nУстановка соединения...");

            if (fptr.put_DeviceEnabled(true) < 0) {
                checkError(fptr);
            }

            ((TextView)findViewById(R.id.textView)).setText(
                    ((TextView)findViewById(R.id.textView)).getText() +
                            "Ок");

            ((TextView)findViewById(R.id.textView)).setText(
                    ((TextView)findViewById(R.id.textView)).getText() +
                            "\nПроверка связи...");
            if (fptr.GetStatus() < 0) { // читаем данные из устройства
                checkError(fptr);
            }

            ((TextView)findViewById(R.id.textView)).setText(
                    ((TextView)findViewById(R.id.textView)).getText() +
                            "Ок");


            // можно получить доступ к данным (get_Date(), get_Time() и тд)
            ((TextView)findViewById(R.id.textView)).setText(
                    ((TextView)findViewById(R.id.textView)).getText() +
                            "\n" + fptr.get_Date().toString() + "   " + fptr.get_Time().toString());


        } catch (Exception e) {
            ((TextView)findViewById(R.id.textView)).setText(e.toString());

        } finally {
            fptr.destroy();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==REQUEST_SHOW_SETTINGS){ // обработчик завершения SettingsActivity.DEVICE_SETTINGS
            if(resultCode==RESULT_OK){
                String settings = data.getExtras().getString(SettingsActivity.DEVICE_SETTINGS);
                ((TextView)findViewById(R.id.textView)).setText(settings);
                setSettings(settings);
            }
            else{
                ((TextView)findViewById(R.id.textView)).setText("Ошибка");
            }
        }
        else{
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
