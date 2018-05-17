package com.example.levcolex.helloatol;

        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.os.Bundle;
        import android.widget.TextView;
        import android.os.AsyncTask;

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

       //((TextView)findViewById(R.id.textView)).setText("опс\n" +"Привет");

//        try{
//            fptr = new Fptr();
//            fptr.create(this);
//        } catch (NullPointerException ex){
//            fptr = null;
//       }


    }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==REQUEST_SHOW_SETTINGS){
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
