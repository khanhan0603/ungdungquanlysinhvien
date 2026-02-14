package vn.edu.stu.luongminhkhanhan_dh52200299;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import vn.edu.stu.luongminhkhanhan_dh52200299.model.Lop;
import vn.edu.stu.luongminhkhanhan_dh52200299.model.Sinhvien;

public class EditThongTinSinhVien extends AppCompatActivity {
    public static final String DB_NAME="qlsinhvien.sqlite";
    public static final String DB_PATH_SUFFIX="/databases/";

    EditText edtMa,edtTen,edtSothich,edtDiem;

    ImageView imghinhanh;

    Button btnThoat,btnLuu,btnChonanh;

    Sinhvien sv=null;

    Spinner spnLop;

    ArrayList<Lop> dsLop;

    ArrayAdapter<Lop> adapterLop;
    int malop_cua_sv = -1;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_thong_tin_sinh_vien);

        // Register launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        try {
                            Uri selectedImage = result.getData().getData();
                            InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imghinhanh.setImageBitmap(bitmap);
                            inputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        copyDbFromAssets();
        addControls();
        getDataFromIntent();    // Lấy mã lớp trước
        loadLopToSpinner();     // Sau đó mới load spinner
        addEvents();
        // Gán sự kiện chọn ảnh
        btnChonanh.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });
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

    private void loadLopToSpinner() {
        dsLop = new ArrayList<>();  // ⭐ QUAN TRỌNG: KHỞI TẠO DANH SÁCH

        SQLiteDatabase database = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("SELECT malop, tenlop FROM lop", null);

        while (cursor.moveToNext()) {
            int malop = cursor.getInt(0);
            String tenlop = cursor.getString(1);
            dsLop.add(new Lop(malop, tenlop));   // ⭐ THÊM VÀO dsLop
        }

        cursor.close();
        database.close();

        // ⭐ KHỞI TẠO ADAPTER SAU KHI CÓ dsLop
        adapterLop = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,   // layout hiển thị khi spinner đóng
                dsLop
        );

        adapterLop.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spnLop.setAdapter(adapterLop);

        // ⭐ Chọn lớp đúng của sinh viên
        for (int i = 0; i < dsLop.size(); i++) {
            if (dsLop.get(i).getMalop() == malop_cua_sv) {
                spnLop.setSelection(i);
                break;
            }
        }
    }

    private void getDataFromIntent() {
        int masv = getIntent().getIntExtra("MASV", -1);
        if (masv == -1) return;

        // Mở DB và lấy lại dữ liệu sinh viên
        SQLiteDatabase db = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

        Cursor cursor = db.rawQuery(
                "SELECT masv, ten, phanloai, hinhanh, diem, sothich FROM sinhvien WHERE masv = ?",
                new String[]{ String.valueOf(masv) }
        );

        if (cursor.moveToFirst()) {
            sv = new Sinhvien(
                    cursor.getInt(0),      // masv
                    cursor.getString(1),   // ten
                    cursor.getInt(2),      // phanloai
                    cursor.getBlob(3),     // hinhanh
                    cursor.getDouble(4),   // diem
                    cursor.getString(5)    // sothich
            );

            // hiển thị lên giao diện
            edtMa.setText(sv.getMasv()+"");
            edtTen.setText(sv.getTen());
            edtDiem.setText(sv.getDiem()+"");
            edtSothich.setText(sv.getSothich());
            malop_cua_sv = sv.getPhanloai();

            byte[] hinh = sv.getHinhanh();
            if (hinh != null && hinh.length > 0) {
                Bitmap bm = BitmapFactory.decodeByteArray(hinh, 0, hinh.length);
                imghinhanh.setImageBitmap(bm);
            }
        }

        cursor.close();
        db.close();
    }

    private void addEvents() {
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyThoat();
            }
        });
        btnLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyLuu();
            }
        });
    }

    private void xuLyLuu() {
        String ten=edtTen.getText().toString();
        double diem= Double.parseDouble(edtDiem.getText().toString());
        String soThich=edtSothich.getText().toString();
        Lop lop= (Lop) spnLop.getSelectedItem();
        int malop=lop.getMalop();

        Drawable drawable = imghinhanh.getDrawable();
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }

        byte[] anh = null;
        if (bitmap != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            anh = bos.toByteArray();
        }

        if(sv==null){
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
            row.put("ten",ten);
            row.put("diem",diem);
            row.put("sothich",soThich);
            row.put("phanloai",malop);
            row.put("hinhanh",anh);
            //insert vào db
            long newID=database.insert(
                    "sinhvien",
                    null,//nếu dữ liệu null thì xử lý sao -> để null
                    row
            );
            //Đóng db
            database.close();
            //thông báo
            Toast.makeText(this,getString(R.string.str_alert_mess_add_student)+" " + newID,Toast.LENGTH_LONG).show();
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
            row.put("ten",ten);
            row.put("diem",diem);
            row.put("sothich",soThich);
            row.put("phanloai",malop);
            row.put("hinhanh",anh);
            //Update
            int count=database.update(
                    "sinhvien",
                    row,
                    "masv = ?",//Điều kiện update
                    new String[]{sv.getMasv()+""}//Lấy giá trị cho điều kiện trên
            );
            database.close();
            Toast.makeText(this,getString(R.string.str_toast_update) +" " + count + " "+getString(R.string.str_toast1_update),Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private void xuLyThoat() {
        finish();
    }

    private void addControls() {
        imghinhanh=findViewById(R.id.imghinhanh);
        edtMa=findViewById(R.id.edtMa);
        edtTen=findViewById(R.id.edtTen);
        edtSothich=findViewById(R.id.edtSothich);
        edtDiem=findViewById(R.id.edtDiem);
        btnThoat=findViewById(R.id.btnThoat);
        btnLuu=findViewById(R.id.btnLuu);
        btnChonanh=findViewById(R.id.btnChonanh);
        spnLop=findViewById(R.id.spnLop);
//        adapterLop = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_spinner_item,
//                dsLop
//        );
//        adapterLop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spnLop.setAdapter(adapterLop);
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
            Intent intent = new Intent(EditThongTinSinhVien.this, DanhSachPhanLoai.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.mnuQuanlysinhvien) {
            Intent intent = new Intent(EditThongTinSinhVien.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.mnuAboutactivity) {
            Intent intent = new Intent(EditThongTinSinhVien.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}