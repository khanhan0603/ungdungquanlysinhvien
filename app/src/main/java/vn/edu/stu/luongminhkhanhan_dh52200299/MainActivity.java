package vn.edu.stu.luongminhkhanhan_dh52200299;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import vn.edu.stu.luongminhkhanhan_dh52200299.adapter.SinhVienAdapter;
import vn.edu.stu.luongminhkhanhan_dh52200299.model.Lop;
import vn.edu.stu.luongminhkhanhan_dh52200299.model.Sinhvien;

public class MainActivity extends AppCompatActivity {
    public static final String DB_NAME="qlsinhvien.sqlite";
    public static final String DB_PATH_SUFFIX="/databases/";
    ListView lvSv;
    ArrayAdapter<Sinhvien> adapter;
    Button btnThem;

    FloatingActionButton fabThem;

    ArrayList<Sinhvien> dsSinhvien;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        copyDbFromAssets();//Copy file db từ trong assets vào trong thư mục db của ứng dụng để truy vấn
        addControls();
        addEvents();
        loadDssvFromDb();//Lấy dữ liệu từ db để hiện listview
    }
    private void copyDbFromAssets() {
        //Kiểm tra có file db trong thư mục ứng dụng chưa, nếu có ko copy nữa (chạy khi ứng dụng run lần đầu tiên)
        File dbFile=getDatabasePath(DB_NAME);//Lấy đường dẫn của file db
        if(!dbFile.exists())//Nếu không thấy file -> chép
        {
            File dbDir=new File(getApplicationInfo().dataDir+DB_PATH_SUFFIX);
            if(!dbDir.exists()){
                //Chưa có thư mục đó ->tạo thư mục
                dbDir.mkdir();
            }
            //Chưa có file, nhưng có thư mục -> chép file nhị phân
            try {
                InputStream is = getAssets().open(DB_NAME);
                String outputFilePath=getApplicationInfo().dataDir+DB_PATH_SUFFIX+DB_NAME;
                OutputStream os=new FileOutputStream(outputFilePath);
                byte[] buffer=new byte[1024];
                int length;
                while((length=is.read(buffer))>0)//Đọc dữ liệu từ file input vào trong buffer, đọc được bao nhiêu byte trả về biến length
                {
                    os.write(buffer,0,length);//3 đối số: mảng cần đọc (buffer), độ lệch (0), ghi bao nhiêu byte (số lượng byte được lưu vào biến length)

                }
                //Giải phóng
                os.flush();
                os.close();
                is.close();
            }
            catch (Exception e){
                Log.e("LOI",e.toString());
            }
        }
    }

    private void addControls() {
        lvSv=findViewById(R.id.lvSv);
        dsSinhvien = new ArrayList<>();
        adapter=new SinhVienAdapter(
                MainActivity.this,
                R.layout.item_sinhvien,
                dsSinhvien
        );
        lvSv.setAdapter(adapter);
        fabThem=findViewById(R.id.fabThem);
    }

    private void addEvents() {
        lvSv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>=0&&position<adapter.getCount()){
                    xuLyXoa(position);
                }
                return true;
            }
        });
        fabThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyThem();
            }
        });
        lvSv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>=0&&position<adapter.getCount()){
                    xuLySua(position);
                }
            }
        });

    }

    private void xuLySua(int position) {
        Sinhvien sv=adapter.getItem(position);
        Intent intent=new Intent(
                MainActivity.this,
                EditThongTinSinhVien.class
        );
        //intent.putExtra("SINHVIEN",sv);
        intent.putExtra("MASV", sv.getMasv());
        startActivity(intent);
    }

    private void xuLyThem() {
        Intent intent=new Intent(
                MainActivity.this,
                EditThongTinSinhVien.class
        );
        startActivity(intent);
    }

    private void xuLyXoa(int position) {
        Sinhvien s=adapter.getItem(position);

        // Hộp thoại xác nhận trước khi xóa
        new AlertDialog.Builder(this)
                .setTitle(R.string.str_tittle_delete)
                .setMessage(getString(R.string.str_message_delete)+ s.getTen() + " ?")
                .setCancelable(true)
                .setPositiveButton(R.string.str_pos_delete, (dialog, which) -> {

                    SQLiteDatabase database = openOrCreateDatabase(
                            DanhSachPhanLoai.DB_NAME,
                            MODE_PRIVATE,
                            null
                    );

                    //Thực hiện xóa
                    int count = database.delete(
                            "sinhvien",
                            "masv = ?",
                            new String[]{ String.valueOf(s.getMasv()) }
                    );

                    new AlertDialog.Builder(this)
                            .setTitle(R.string.str_alert_delete)
                            .setMessage(getString(R.string.str_alert_mess_delete) + count +getString(R.string.str_alert_mess2_delete))
                            .setPositiveButton(R.string.str_alert_button_delete, null)
                            .show();

                    loadDssvFromDb();
                })
                .setNegativeButton(R.string.str_negative_delete, null)

                .show();
    }

    private void loadDssvFromDb() {
        //Khi nào có db truyền vào -> mở. Nếu tìm không thấy tên db -> tạo mới theo đúng tên truyền (lỗi)
        SQLiteDatabase database=openOrCreateDatabase(
                DB_NAME,
                MODE_PRIVATE,
                null
        );
        //cursor đếm từ 0
        //truy vấn không điều kiện
        //Cursor cursor=database.rawQuery("Select * From sinhvien",null);
        //truy vấn có điều kiện
        //Cursor cursor=database.rawQuery("Select * From sinhvien Where lop=?", new String[]{"Lớp 2"});
        //truy vấn kiểu mới, chỉ cho truy vấn trên một bảng
        // JOIN để lấy tên lớp
        String sql = "SELECT sv.masv, sv.ten, sv.phanloai,sv.hinhanh,sv.diem,sv.sothich, l.tenlop " +
                "FROM sinhvien sv JOIN lop l ON sv.phanloai = l.malop";
        Cursor cursor = database.rawQuery(sql, null);
        adapter.clear();
        while (cursor.moveToNext()) {
            Sinhvien sv = new Sinhvien();
            sv.setMasv(cursor.getInt(0));
            sv.setTen(cursor.getString(1));
            sv.setPhanloai(cursor.getInt(2));// vẫn lưu mã lớp
            sv.setHinhanh(cursor.getBlob(3));
            sv.setDiem(cursor.getDouble(4));
            sv.setSothich(cursor.getString(5));

            // Lấy tên lớp từ cột số 3
            String tenlop = cursor.getString(6);

            // Tạo object Lop
            Lop lop = new Lop(sv.getPhanloai(), tenlop);

            // Gán vào Sinhvien
            sv.setLop(lop);

            // Thêm vào list
            dsSinhvien.add(sv);
        }
        cursor.close();
        database.close();
        adapter.notifyDataSetChanged();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Hàm xảy ra khi activity có focus
        //Load lại dữ liệu mới
        loadDssvFromDb();
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
            Intent intent = new Intent(MainActivity.this, DanhSachPhanLoai.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.mnuQuanlysinhvien) {
            return true;
        }
        if (id == R.id.mnuAboutactivity) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}