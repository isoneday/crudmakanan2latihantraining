package com.training.crudmakananapp.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.training.crudmakananapp.R;
import com.training.crudmakananapp.adapter.ListMakananAdapter;
import com.training.crudmakananapp.helper.MyConstant;
import com.training.crudmakananapp.helper.MyFuction;
import com.training.crudmakananapp.helper.SessionManager;
import com.training.crudmakananapp.model.DataKategoriItem;
import com.training.crudmakananapp.model.DataMakananItem;
import com.training.crudmakananapp.model.ResponseDataMakanan;
import com.training.crudmakananapp.model.ResponseKategorimakan;
import com.training.crudmakananapp.network.MyRetrofitClient;
import com.training.crudmakananapp.network.RestApi;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.training.crudmakananapp.helper.MyConstant.STORAGE_PERMISSION_CODE;

public class MakananActivity extends MyFuction implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.spincarimakanan)
    Spinner spincarimakanan;
    @BindView(R.id.listmakanan)
    RecyclerView listmakanan;
    @BindView(R.id.refreshlayout)
    SwipeRefreshLayout refreshlayout;
    private Dialog dialog;
    private TextInputEditText edtnamamakanan;
    private Button btnuploadmakanan;
    private ImageView imgpreview;
    private Button btninsert;
    private Button btnreset;
    private Spinner spinnercarikategori;
    private Uri filepath;
    private Bitmap bitmap;
    private String strkategori;
    private String strnamamakan;
    private String strpath;
    private String striduser;
    private String strtime;
    private SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makanan);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        storagepermission();
        getkategorimakanan(spincarimakanan);
        refreshlayout.setOnRefreshListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(MakananActivity.this);
                dialog.setContentView(R.layout.tambahmakanan);
                dialog.setTitle("tambah makanan");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(false);
                //inisialisasi
                edtnamamakanan = (TextInputEditText) dialog.findViewById(R.id.edtnamamakanan);
                btnuploadmakanan = (Button) dialog.findViewById(R.id.btnuploadmakanan);
                imgpreview = (ImageView) dialog.findViewById(R.id.imgupload);
                btninsert = (Button) dialog.findViewById(R.id.btninsert);
                btnreset = (Button) dialog.findViewById(R.id.btnreset);
                spinnercarikategori = (Spinner) dialog.findViewById(R.id.spincarikategori);
                getkategorimakanan(spinnercarikategori);
                //aksi
                btnuploadmakanan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        readstorage(MyConstant.REQ_FILE_CHOOSE);
                    }
                });
                btninsert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        strnamamakan = edtnamamakanan.getText().toString();
                        if (TextUtils.isEmpty(strnamamakan)) {
                            edtnamamakanan.setError("nama makanan tidak boleh kosong");
                            edtnamamakanan.requestFocus();
                            myanimation(edtnamamakanan);
                        } else if (imgpreview.getDrawable() == null) {
                            myToast("gambar harus dipilih");
                        } else {
                            insertdatamakanan(strkategori);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    private void storagepermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                myToast("Permission granted now you can read the storage");
            } else {
                //Displaying another toast if permission is not granted
                myToast("Oops you just denied the permission");
            }
        }
    }

    private void insertdatamakanan(String strkategori) {
        //mengambil path dari gmbar yang d i upload
        try {
            strpath = getPath(filepath);
            SessionManager sessionManager = new SessionManager(MakananActivity.this);
            striduser = sessionManager.getIdUser();
//            MaxSizeImage(strpath);

        } catch (Exception e) {
            myToast("gambar terlalu besar \n silahkan pilih gambar yang lebih kecil");
            e.printStackTrace();
        }
        /**
         * Sets the maximum time to wait in milliseconds between two upload attempts.
         * This is useful because every time an upload fails, the wait time gets multiplied by
         * {@link UploadService#BACKOFF_MULTIPLIER} and it's not convenient that the value grows
         * indefinitely.
         */
        strtime = currentDate();
        try {
            new MultipartUploadRequest(c, MyConstant.UPLOAD_URL)
                    .addFileToUpload(strpath, "image")
                    .addParameter("vsiduser", striduser)
                    .addParameter("vsnamamakanan", strnamamakan)
                    .addParameter("vstimeinsert", strtime)
                    .addParameter("vskategori", strkategori)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();

            // getDataMakanan(strkategorimakanan);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            myToast(e.getMessage());
        } catch (FileNotFoundException e) {
            myToast(e.getMessage());
            e.printStackTrace();
        }

    }

    private String getPath(Uri filepath) {
        Cursor cursor = getContentResolver().query(filepath, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    private void getkategorimakanan(final Spinner spinnercarikategori) {
        RestApi api = MyRetrofitClient.getInstaceRetrofit();
        Call<ResponseKategorimakan> kategorimakanCall = api.getkategorimakanan();
        kategorimakanCall.enqueue(new Callback<ResponseKategorimakan>() {
            @Override
            public void onResponse(Call<ResponseKategorimakan> call, Response<ResponseKategorimakan> response) {
                List<DataKategoriItem> dataKategori = response.body().getDataKategori();
                String[] itemid = new String[dataKategori.size()];
                String[] itemnama = new String[dataKategori.size()];
                for (int i = 0; i < dataKategori.size(); i++) {
                    itemid[i] = dataKategori.get(i).getIdKategori();
                    itemnama[i] = dataKategori.get(i).getNamaKategori();
                }
                ArrayAdapter adapter = new ArrayAdapter(c, android.R.layout.simple_spinner_dropdown_item, itemnama);
                spinnercarikategori.setAdapter(adapter);
                spinnercarikategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        strkategori = parent.getItemAtPosition(position).toString();
                        getdatamakanan(strkategori);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseKategorimakan> call, Throwable t) {

            }
        });
    }

    private void getdatamakanan(String strkategori) {
        final ProgressDialog dialog = ProgressDialog.show(c, "proress getdatamakamnan", "loading. . . .");
        RestApi api = MyRetrofitClient.getInstaceRetrofit();
        manager = new SessionManager(MakananActivity.this);
        String iduser = manager.getIdUser();
        Call<ResponseDataMakanan> dataMakananCall = api.getdatamakanan(
                iduser,
                strkategori
        );
        dataMakananCall.enqueue(new Callback<ResponseDataMakanan>() {
            @Override
            public void onResponse(Call<ResponseDataMakanan> call, Response<ResponseDataMakanan> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    List<DataMakananItem> dataMakanan = response.body().getDataMakanan();
                    String[] idmakanan = new String[dataMakanan.size()];
                    String[] namamakanan = new String[dataMakanan.size()];
                    for (int i = 0; i < dataMakanan.size(); i++) {
                        idmakanan[i] = dataMakanan.get(i).getIdMakanan();
                        namamakanan[i] = dataMakanan.get(i).getNama();
                    }
                    listmakanan.setLayoutManager(new LinearLayoutManager(MakananActivity.this));
                    ListMakananAdapter adapter = new ListMakananAdapter(c, dataMakanan);
                    listmakanan.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ResponseDataMakanan> call, Throwable t) {

            }
        });

    }

    private void readstorage(int reqFileChoose) {
        Intent intentgalery = new Intent(Intent.ACTION_PICK);
        intentgalery.setType("image/*");
        intentgalery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentgalery, "select Pictures"), reqFileChoose);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyConstant.REQ_FILE_CHOOSE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filepath = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imgpreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

    @Override
    public void onRefresh() {
        getdatamakanan(strkategori);
        refreshlayout.setRefreshing(false);
    }
}
