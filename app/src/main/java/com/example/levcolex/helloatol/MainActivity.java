package com.example.levcolex.helloatol;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;

        import com.atol.drivers.fptr.Fptr;
        import com.atol.drivers.fptr.IFptr;
        import com.atol.drivers.fptr.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    IFptr fptr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{
            fptr = new Fptr();
            fptr.create(this);
        } catch (NullPointerException ex){
            fptr = null;
        }

    }
}
