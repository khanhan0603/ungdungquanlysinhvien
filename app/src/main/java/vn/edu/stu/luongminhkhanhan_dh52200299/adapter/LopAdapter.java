package vn.edu.stu.luongminhkhanhan_dh52200299.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import vn.edu.stu.luongminhkhanhan_dh52200299.R;
import vn.edu.stu.luongminhkhanhan_dh52200299.model.Lop;

public class LopAdapter extends ArrayAdapter<Lop> {
    Activity context;
    int resource;
    List<Lop> objects;
    public LopAdapter(@NonNull Activity context, int resource, @NonNull List<Lop> objects) {
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
        TextView edtMalop=item.findViewById(R.id.edtMalop);
        TextView edtTenlop=item.findViewById(R.id.edtTenlop);
        Lop l=this.objects.get(position);
        edtMalop.setText(l.getMalop()+"");
        edtTenlop.setText(l.getTenlop());
        return item;
    }
}
