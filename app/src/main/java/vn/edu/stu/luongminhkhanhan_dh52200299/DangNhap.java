package vn.edu.stu.luongminhkhanhan_dh52200299;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DangNhap extends AppCompatActivity {
    EditText edtUser,edtPass;
    Button btnDN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        addEvents();
    }
    private void addEvents() {
        btnDN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=edtUser.getText().toString();
                String pass=edtPass.getText().toString();
                if(user.equals("admin")&&pass.equals("123")){
                    Intent intent=new Intent(
                            DangNhap.this,
                            DanhSachPhanLoai.class
                    );
                    startActivity(intent);
                    finish(); // đóng màn hình login, không cho quay lại
                }
                else {
                    Toast.makeText(DangNhap.this, R.string.str_alert_mess_login_fail, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addControls() {
        edtUser=findViewById(R.id.edtUser);
        edtPass=findViewById(R.id.edtPass);
        btnDN=findViewById(R.id.btnDN);
    }
}