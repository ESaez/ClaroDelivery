package com.autentia.clarodelivery;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ResultActivity extends AppCompatActivity {

    private EditText et_rut, et_nombre, et_cod_aud, et_email, et_num_ser, et_num_arr, et_nombre_vendedor, et_rut_vendedor;
    private Button bt_guardar_evidencia;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {

            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.mipmap.ic_claro);

            et_rut = (EditText) findViewById(R.id.et_rut_usuario);
            et_rut_vendedor = (EditText) findViewById(R.id.et_rut_vendedor);
            et_nombre_vendedor = (EditText) findViewById(R.id.et_nombre_vendedor);
            et_nombre = (EditText) findViewById(R.id.et_nombre_completo);
            et_cod_aud = (EditText) findViewById(R.id.et_codigo_auditoria);
            et_email = (EditText) findViewById(R.id.et_email_resultado);
            et_num_ser = (EditText) findViewById(R.id.et_numero_servicio);
            et_num_arr = (EditText) findViewById(R.id.et_numero_contrato);
            bt_guardar_evidencia = (Button) findViewById(R.id.bt_guardar_evidencia);


            Bundle result = getIntent().getExtras();

            et_nombre_vendedor.setText(result.getString("NOMBRE_VENDEDOR"));
            et_rut_vendedor.setText(result.getString("RUT_VENDEDOR"));
            et_rut.setText(result.getString("RUT"));
            et_nombre.setText(result.getString("NOMBRE"));
            et_cod_aud.setText(result.getString("CODIGO_AUDITORIA"));
            et_email.setText(result.getString("EMAIL"));
            et_num_ser.setText(result.getString("NUMERO_SERVICIO"));
            et_num_arr.setText(result.getString("NUMERO_ARRIENDO"));
            bt_guardar_evidencia.setEnabled(false);
//
//
//
            ((EditText) findViewById(R.id.et_status))
                    .setText("" + new SimpleDateFormat("yyyy-MM-dd HH:mm")
                            .format(new Date()) + " / " + ConectionManager.getCarrierName(this) + " - " + ConectionManager.getTypeConnection(this) + " - " + ConectionManager.getLevelConnection(this));
//

            bt_guardar_evidencia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();

                }
            });


        }
    }

    public static Bitmap getScreenShot(View view) {

        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;

    }

    /***
     * Almancenamiento de imagen
     *
     * @param bm
     * @param fileName
     */
    public static void store(Bitmap bm, String fileName) throws IOException {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Claro/Evidencias/" + new SimpleDateFormat("yyyyMM").format(new Date());
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dirPath, fileName);

        FileOutputStream fOut = new FileOutputStream(file);
        bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
        fOut.flush();
        fOut.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_resultado, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.accion_guardar_evidencia) {

            bt_guardar_evidencia.setEnabled(true);
            bt_guardar_evidencia.setBackgroundColor(getResources().getColor(R.color.colorPrimary_claro));
            rootView = getWindow().getDecorView();//.findViewById(android.R.id.content);

            if (ActivityCompat.checkSelfPermission(ResultActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(ResultActivity.this)
                        .setTitle("Permisos para la aplicación")
                        .setMessage("La aplicación necesita permisos para guardar la evidencia")
                        .setPositiveButton("Ok", null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {

                                ActivityCompat.requestPermissions(ResultActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            }
                        }).create().show();
            } else {
                try {
                    store(getScreenShot(rootView), String.format("%s_%s_%s_%s_%s.png", new SimpleDateFormat("yyyyMMdd_HH-mm")
                            .format(new Date()), et_cod_aud.getText().toString(), et_rut.getText().toString(), et_num_ser.getText().toString(), et_num_arr.getText().toString()));
                    new AlertDialog.Builder(ResultActivity.this)
                            .setTitle("Exito")
                            .setMessage("Evidencia generada exitosamente")
                            .setPositiveButton("Aceptar", null)
                            .setCancelable(false)
                            .create().show();

                } catch (Exception e) {
                    Toast.makeText(ResultActivity.this, "Ha ocurrido un error, reintente guardar evidencia", Toast.LENGTH_LONG).show();
                }

            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1:

                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];

                    if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) || permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {

                            try {
                                store(getScreenShot(rootView), String.format("%s_%s_%s_%s_%s.png", new SimpleDateFormat("yyyyMMdd_HH-mm").format(new Date()), et_cod_aud.getText().toString(), et_rut.getText().toString(), et_num_ser.getText().toString(), et_num_arr.getText().toString()));
                                new AlertDialog.Builder(ResultActivity.this)
                                        .setTitle("Exito")
                                        .setMessage("Evidencia almacenada correctamente")
                                        .setPositiveButton("Aceptar", null)
                                        .setCancelable(false)
                                        .create().show();

                            } catch (Exception e) {
                                Toast.makeText(ResultActivity.this, "Ha ocurrido un error, reintente guardar evidencia", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            ActivityCompat.requestPermissions(ResultActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
