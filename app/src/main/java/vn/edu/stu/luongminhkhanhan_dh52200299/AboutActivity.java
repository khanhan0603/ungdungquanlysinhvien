package vn.edu.stu.luongminhkhanhan_dh52200299;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AboutActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    TextView edtSdt;

    Button btnDial,btnCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        addControls();
        addEvents();
    }
    private void addEvents() {
        btnDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDial();
            }
        });
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndCall();
            }
        });
    }
    private void checkPermissionAndCall() {
        try {
            if(ActivityCompat.checkSelfPermission(
                    AboutActivity.this,
                    Manifest.permission.CALL_PHONE
            )!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(
                        AboutActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        100
                );
            }
            String phone=edtSdt.getText().toString();
            Intent intent=new Intent(
                    Intent.ACTION_CALL,
                    Uri.parse("tel:"+phone)
            );
            startActivity(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==100){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                checkPermissionAndCall();
            }
        }
    }

    private void doDial() {
        String phone=edtSdt.getText().toString();
        Intent intent=new Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:"+phone)
        );
        startActivity(intent);
    }

    private void addControls() {
        edtSdt=findViewById(R.id.edtSdt);
        btnDial=findViewById(R.id.btnDial);
        btnCall=findViewById(R.id.btnCall);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng stu = new LatLng(10.738148554983699, 106.67784570456776);
        mMap.addMarker(new MarkerOptions().position(stu).title("Marker in STU"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(stu,18));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mnu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mnuQuanlyphanloai) {
            Intent intent = new Intent(AboutActivity.this, DanhSachPhanLoai.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.mnuQuanlysinhvien) {
            Intent intent = new Intent(AboutActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.mnuAboutactivity) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}