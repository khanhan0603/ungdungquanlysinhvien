package vn.edu.stu.luongminhkhanhan_dh52200299.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import vn.edu.stu.luongminhkhanhan_dh52200299.R;
import vn.edu.stu.luongminhkhanhan_dh52200299.model.Sinhvien;

public class SinhVienAdapter extends ArrayAdapter<Sinhvien> {
    Activity context;
    int resource;
    List<Sinhvien> objects;

    public SinhVienAdapter(@NonNull Activity context, int resource, @NonNull List<Sinhvien> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.objects=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View item=null;
        item=this.context.getLayoutInflater().inflate(
                this.resource,
                null
        );
        ImageView imghinhanh=item.findViewById(R.id.imghinhanh);
        TextView edtMasv=item.findViewById(R.id.edtMasv);
        TextView edtTensv=item.findViewById(R.id.edtTensv);
        TextView edtPhanloai=item.findViewById(R.id.edtPhanloai);
        Sinhvien sv=this.objects.get(position);
        byte[] hinh = sv.getHinhanh();
        if(hinh != null && hinh.length > 0) {
            Bitmap bm = BitmapFactory.decodeByteArray(hinh, 0, hinh.length);
            imghinhanh.setImageBitmap(bm);
        }
        edtMasv.setText(sv.getMasv()+"");
        edtTensv.setText(sv.getTen());
        edtPhanloai.setText(sv.getLop().getTenlop());
        return item;
    }
}
