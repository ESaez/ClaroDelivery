package com.autentia.clarodelivery;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

public class FormularioActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText et_rut_usuario, et_num_solicitud, et_numero_arriendo, et_email;
    private String rut;
    private int rut_numero;
    private char rut_dv;
    private int REQUEST_INTENT_CODE = 0000002;
    private static final String TAG = "RESULT";
    private String nombreVendedor,rutVendedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        try {
            nombreVendedor = getIntent().getExtras().getString("NOMBRE_VENDEDOR");
            rutVendedor = getIntent().getExtras().getString("RUT_VENDEDOR");
        } catch (Exception e) {

        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.mipmap.ic_claro);
        }

        et_rut_usuario = (EditText) findViewById(R.id.et_rut_usuario);
        et_num_solicitud = (EditText) findViewById(R.id.et_numero_servicio);
        et_numero_arriendo = (EditText) findViewById(R.id.et_numero_contrato);
        et_email = (EditText) findViewById(R.id.et_email);

        findViewById(R.id.bt_cancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.bt_verifica_identidad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(),ResultActivity.class));

                if (validacionDatos()) {

                    String[] rut_divided = rut_divided();
                    rut_numero = Integer.parseInt(rut_divided[0]);
                    rut_dv = (rut_divided[1]).charAt(0);

                    if (!validaRutDV(rut_numero, rut_dv)) {
                        et_rut_usuario.setError("Rut inválido");
                    } else {
                        et_rut_usuario.setError(null);
                        IntentVerificaIdentidad(rut_numero, rut_dv);
                    }
                }
            }
        });
    }

    private boolean validacionDatos() {

        if (TextUtils.isEmpty(et_rut_usuario.getText())) {
            et_rut_usuario.setError("Sin datos");
            et_rut_usuario.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(et_email.getText())) {
            et_email.setError("Sin datos");
            et_email.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(et_num_solicitud.getText())) {
            et_num_solicitud.setError("Sin datos");
            et_num_solicitud.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bundle result = data.getExtras();

        if (requestCode == REQUEST_INTENT_CODE) {

            boolean identidadVerificada = false;
            String nombre = "";
            String codigoAuditoria = "";

            if (resultCode == RESULT_OK) {
                Log.d(TAG, "RESULT_OK");

                Iterator iterator = data.getExtras().keySet().iterator();
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
                    if (key.equalsIgnoreCase("codigoAuditoria")) {
                        codigoAuditoria = value.toString();
                    }

                }
            } else if (resultCode == RESULT_CANCELED) Log.d(TAG, "RESULT_CANCELED");

            if (identidadVerificada) {

                Intent intent = new Intent(this, ResultActivity.class);
                intent.putExtra("NOMBRE", nombre);
                intent.putExtra("NOMBRE_VENDEDOR", nombreVendedor);
                intent.putExtra("RUT_VENDEDOR", rutVendedor);
                intent.putExtra("CODIGO_AUDITORIA", codigoAuditoria);
                intent.putExtra("RUT", et_rut_usuario.getText().toString());
                intent.putExtra("NUMERO_SERVICIO", et_num_solicitud.getText().toString());
                intent.putExtra("NUMERO_ARRIENDO", et_numero_arriendo.getText().toString());
                intent.putExtra("EMAIL", et_email.getText().toString());

                new AlertDialog.Builder(FormularioActivity.this)
                        .setTitle("Verificacion de cliente")
                        .setMessage("Verificación realizada exitosamente")
                        .setIcon(R.mipmap.ic_exito)
                        .setPositiveButton("Continuar", null)
                        .create().show();

                startActivity(intent);

            } else {
                //Muestra mensaje
                Toast.makeText(this, "Cliente no verificado", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    public String[] rut_divided() {
        rut = et_rut_usuario.getText().toString().replace("-", "");
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

    @Override
    protected void onResume() {
        super.onResume();
        et_rut_usuario.requestFocus();
        et_email.setText("");
        et_num_solicitud.setText("");
        et_rut_usuario.setText("");
        et_numero_arriendo.setText("");

    }

    /**
     * @param rut
     * @param dv
     */


    public void IntentVerificaIdentidad(int rut, char dv) {

        String color_primario = getResources().getString(R.color.colorPrimary_claro);
        String color_primario_oscuro = getResources().getString(R.color.colorPrimaryDark_claro);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_claro);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        String claro = "Verificación de identidad para contratación de servicios móviles Claro Chile";

        Intent intent = new Intent("acepta.intent.action.VERIFICA_IDENTIDAD");
        Bundle bl = new Bundle();
        bl.putInt("RUT", rut);
        bl.putChar("DV", dv);
        bl.putString("COLOR_PRIMARY", color_primario);
        bl.putString("COLOR_PRIMARY_DARK", color_primario_oscuro);
        bl.putString("TITULO_VERIFICACION",claro);
        intent.putExtra("ICON", byteArray);
        intent.putExtras(bl);
        startActivityForResult(intent, REQUEST_INTENT_CODE);

    }

}
