package vn.edu.stu.luongminhkhanhan_dh52200299;

import android.app.AlertDialog;
import android.content.ContentValues;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import vn.edu.stu.luongminhkhanhan_dh52200299.adapter.LopAdapter;
import vn.edu.stu.luongminhkhanhan_dh52200299.model.Lop;

public class DanhSachPhanLoai extends AppCompatActivity {
    public static final String DB_NAME="qlsinhvien.sqlite";
    public static final String DB_PATH_SUFFIX="/databases/";

    ListView lvPhanloai;
    ArrayAdapter<Lop> adapter;
    EditText edtMalop,edtTenlop;
    Button btnLuu,btnClear;

    ArrayList<Lop> dsLop;
    Lop l=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_danh_sach_phan_loai);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        copyDbFromAssets();//Copy file db từ trong assets vào trong thư mục db của ứng dụng để truy vấn
        addControls();
        addEvents();
        loadDsplFromDb();//Lấy dữ liệu từ db để hiện listview
    }
    private void loadDsplFromDb() {
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
        Cursor cursor=database.query(
                "lop",
                null,//nếu muốn lấy một số cột xác định: new String[]{"ma","ten"}
                null,//nếu có điều kiện thì: "lop = ? and ma = ?"
                null,//giá trị cho các hỏi chấm nếu có giá trị điều kiện ở trên: new String[]{"Lớp 1", "5"}
                null,//có groupby ko?
                null,//có having ko?
                null //có orderby ko? "ma asc"
        );
        adapter.clear();
        while (cursor.moveToNext()){
            int malop=cursor.getInt(0);
            String tenlop=cursor.getString(1);
            adapter.add(new Lop(malop,tenlop));
        }
        cursor.close();
        database.close();
        adapter.notifyDataSetChanged();

    }

    private void addEvents() {
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyLuu();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyClear();
            }
        });
        lvPhanloai.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>=0&&position<adapter.getCount()){
                    xuLySua(position);
                }
            }
        });
        lvPhanloai.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(position>=0&&position<adapter.getCount()){
                    xuLyXoa(position);
                }
                return true;
            }
        });
    }

    private void xuLySua(int position) {
        l=adapter.getItem(position);
        edtMalop.setText(l.getMalop()+"");
        edtTenlop.setText(l.getTenlop());
    }

    private void xuLyXoa(int position) {

        Lop l = adapter.getItem(position);

        // Hộp thoại xác nhận trước khi xóa
        new AlertDialog.Builder(this)
                .setTitle(R.string.str_tittle_delete)
                .setMessage(getString(R.string.str_message_delete_class)+" " + l.getTenlop() + " ?")
                .setCancelable(true)
                .setPositiveButton(R.string.str_pos_delete, (dialog, which) -> {

                    SQLiteDatabase database = openOrCreateDatabase(
                            DanhSachPhanLoai.DB_NAME,
                            MODE_PRIVATE,
                            null
                    );

                    // KIỂM TRA: lớp có sinh viên không?
                    Cursor cursor = database.rawQuery(
                            "SELECT COUNT(*) FROM sinhvien WHERE phanloai = ?",
                            new String[]{ String.valueOf(l.getMalop()) }
                    );
                    cursor.moveToFirst();
                    int soSinhVien = cursor.getInt(0);
                    cursor.close();

                    // Nếu có sinh viên → hiện thông báo cấm xóa
                    if (soSinhVien > 0) {
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.str_alert_no_delete)
                                .setMessage(getString(R.string.str_alert_mess_delete_class) + soSinhVien + getString(R.string.str_alert_mess2_delete) +"\n"+getString(R.string.str_alert_mess_delete2_class))
                                .setPositiveButton(R.string.str_alert_button_delete, null)
                                .show();
                        return;
                    }

                    // Không có sinh viên → cho phép xóa
                    int count = database.delete(
                            "lop",
                            "malop = ?",
                            new String[]{ String.valueOf(l.getMalop()) }
                    );

                    database.close();

                    new AlertDialog.Builder(this)
                            .setTitle(R.string.str_alert_delete)
                            .setMessage(getString(R.string.str_alert_mess_delete)+ count + getString(R.string.str_alert_mess_delete3_class))
                            .setPositiveButton(R.string.str_alert_button_delete, null)
                            .show();

                    loadDsplFromDb();
                })

                .setNegativeButton(R.string.str_negative_delete, null)

                .show();
    }

    private void xuLyLuu() {
        String tenlop=edtTenlop.getText().toString();
        if(l==null){
            //Thêm
            //Mở db
            SQLiteDatabase database=openOrCreateDatabase(
                    DanhSachPhanLoai.DB_NAME,
                    MODE_PRIVATE,
                    null
            );
            //Dòng dữ liệu để thêm
            ContentValues row=new ContentValues();
            //put dữ liệu vào row
            row.put("tenlop",tenlop);
            //insert vào db
            long newID=database.insert(
                    "lop",
                    null,//nếu dữ liệu null thì xử lý sao -> để null
                    row
            );
            //Đóng db
            database.close();
            //thông báo
            Toast.makeText(
                    DanhSachPhanLoai.this,
                    getString(R.string.str_alert_mess_add_class) +" " + newID,
                    Toast.LENGTH_LONG
            ).show();
        }
        else{
            //Sửa
            //Mở db
            SQLiteDatabase database=openOrCreateDatabase(
                    DanhSachPhanLoai.DB_NAME,
                    MODE_PRIVATE,
                    null
            );
            //Dòng dữ liệu để thêm
            ContentValues row=new ContentValues();
            //put dữ liệu vào row
            row.put("tenlop",tenlop);
            //Update
            int count=database.update(
                    "lop",
                    row,
                    "malop = ?",//Điều kiện update
                    new String[]{l.getMalop()+""}//Lấy giá trị cho điều kiện trên
            );
            database.close();
            Toast.makeText(this,getString(R.string.str_toast_update) +" " + count + " "+getString(R.string.str_toast1_update),Toast.LENGTH_LONG).show();
        }
        loadDsplFromDb();
        edtMalop.setText("");
        edtTenlop.setText("");
        edtMalop.clearFocus();
        edtTenlop.clearFocus();
        l = null; //Reset về trạng thái thêm mới
    }

    private void xuLyClear() {
        edtMalop.setText("");
        edtTenlop.setText("");
        edtMalop.clearFocus();
        edtTenlop.clearFocus();
        l = null; //Reset về trạng thái thêm mới
    }

    private void addControls() {
        edtMalop=findViewById(R.id.edtMalop);
        edtTenlop=findViewById(R.id.edtTenlop);
        lvPhanloai=findViewById(R.id.lvPhanloai);
        dsLop=new ArrayList<>();
        adapter=new LopAdapter(
                DanhSachPhanLoai.this,
                R.layout.item_phanloai,
                dsLop
        );
        lvPhanloai.setAdapter(adapter);
        btnLuu=findViewById(R.id.btnLuu);
        btnClear=findViewById(R.id.btnClear);
    }

    private void copyDbFromAssets() {
        //Kiểm tra có file db trong thư mục ứng dụng chưa, nếu có ko copy nữa (chạy khi ứng dụng run lần đầu tiên)
        File dbFile=getDatabasePath(DB_NAME);//Lấy đường dẫn của file db
        if(!dbFile.exists())//Nếu không thấy file -> chép
        {
            File dbDir = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!dbDir.exists()) {
                //Chưa có thư mục đó ->tạo thư mục
                dbDir.mkdir();
            }
            //Chưa có file, nhưng có thư mục -> chép file nhị phân
            try {
                InputStream is = getAssets().open(DB_NAME);
                String outputFilePath = getApplicationInfo().dataDir + DB_PATH_SUFFIX + DB_NAME;
                OutputStream os = new FileOutputStream(outputFilePath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0)//Đọc dữ liệu từ file input vào trong buffer, đọc được bao nhiêu byte trả về biến length
                {
                    os.write(buffer, 0, length);//3 đối số: mảng cần đọc (buffer), độ lệch (0), ghi bao nhiêu byte (số lượng byte được lưu vào biến length)

                }
                //Giải phóng
                os.flush();
                os.close();
                is.close();
            } catch (Exception e) {
                Log.e("LOI", e.toString());
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Hàm xảy ra khi activity có focus
        //Load lại dữ liệu mới
        loadDsplFromDb();
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
            // Đang ở MainActivity → không mở lại
            return true;
        }

        if (id == R.id.mnuQuanlysinhvien) {
            Intent intent = new Intent(DanhSachPhanLoai.this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.mnuAboutactivity) {
            Intent intent = new Intent(DanhSachPhanLoai.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}