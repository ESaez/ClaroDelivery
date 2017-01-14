package com.autentia.clarodelivery;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText et_rut, et_nombre;
    String rut;
    int rut_numero;
    char rut_dv;
    int REQUEST_INTENT_CODE = 0000001;
    private static final String TAG = "RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.mipmap.ic_claro);
        }

        et_rut = (EditText) findViewById(R.id.et_rut);
        et_nombre = (EditText) findViewById(R.id.et_nombre);



        findViewById(R.id.bt_iniciar_sesion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validaDatos()) {

                    String[] rut_divided = rut_divided();
                    rut_numero = Integer.parseInt(rut_divided[0]);
                    rut_dv = (rut_divided[1]).charAt(0);

                    if (!validaRutDV(rut_numero, rut_dv)) {
                        et_rut.setError("Rut inválido");
                    } else {
                        et_rut.setError(null);
                        IntentVerificaIdentidad(rut_numero, rut_dv);
                    }
                }
            }
        });

        findViewById(R.id.btn_salir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        populatePropertiesView();
    }

    /**
     * @return
     */

    private void populatePropertiesView() {

        StringBuilder sb = new StringBuilder();

        PackageManager manager = getApplication().getPackageManager();
        String version = "";
        try {
            PackageInfo info = manager.getPackageInfo(getApplication().getPackageName(), 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        sb.append(String.format("Versión %s\n", version));

        ((TextView)findViewById(R.id.tv_version)).setText(sb.toString());


    }

    private boolean validaDatos() {
        if (TextUtils.isEmpty(et_rut.getText())) {
            et_rut.setError("Sin datos");
            et_rut.requestFocus();
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bundle result = data.getExtras();

        if (requestCode == REQUEST_INTENT_CODE) {

            if (resultCode == RESULT_OK) {
                Log.d(TAG, "RESULT_OK");

                Iterator iterator = data.getExtras().keySet().iterator();

                boolean identidadVerificada = false;
                String nombre = "";
                String rut_vendedor = "";

                while (iterator.hasNext()) {

                    String key = iterator.next().toString();

                    Object value = result.get(key);
                    Log.d(TAG, String.format("%s : %s", key, value));

                    if (key.equalsIgnoreCase("identidadVerificada")) {
                        identidadVerificada = (boolean) value;
                    }
                    if (key.equalsIgnoreCase("nombre")) {
                        nombre = value.toString();
                    }
                    if (key.equalsIgnoreCase("rut")) {
                        rut_vendedor = value.toString();
                    }
                    if (key.equalsIgnoreCase("dv")){
                        rut_vendedor = rut_vendedor +"-"+value.toString();
                    }
                }

                et_nombre.setText(nombre);

                if (identidadVerificada) {
                    //Pasa a la siguiente
                    Intent intent = new Intent(getApplicationContext(), FormularioActivity.class);
                    intent.putExtra("NOMBRE_VENDEDOR", nombre);
                    intent.putExtra("RUT_VENDEDOR", rut_vendedor);
                    startActivity(intent);
                } else {
                    //Muestra mensaje de error
                    Toast.makeText(this, "Usuario no verificado", Toast.LENGTH_LONG).show();
                }
            } else if (resultCode == RESULT_CANCELED) Log.d(TAG, "RESULT_CANCELED");
        }
    }


    public String[] rut_divided() {
        rut = et_rut.getText().toString().replace("-", "");
        String[] rut_divided = new String[2];
        rut_divided[0] = rut.substring(0, rut.length() - 1);
        rut_divided[1] = rut.substring(rut.length() - 1, rut.length());
        return rut_divided;
    }

    private boolean validaRutDV(int rut, char dv) {
        int m = 0, s = 1;
        for (; rut != 0; rut /= 10) {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
        }
        return dv == (char) (s != 0 ? s + 47 : 75);
    }


    public void IntentVerificaIdentidad(int rut, char dv) {

        String color_primario = getResources().getString(R.color.colorPrimary_claro);
        String color_primario_oscuro = getResources().getString(R.color.colorPrimaryDark_claro);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_claro);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Intent intent = new Intent("acepta.intent.action.VERIFICA_IDENTIDAD");
        Bundle bl = new Bundle();
        bl.putInt("RUT", rut);
        bl.putChar("DV", dv);
        bl.putString("COLOR_PRIMARY", color_primario);
        bl.putString("COLOR_PRIMARY_DARK", color_primario_oscuro);
        intent.putExtra("ICON", byteArray);
        intent.putExtras(bl);
        startActivityForResult(intent, REQUEST_INTENT_CODE);

    }
}
