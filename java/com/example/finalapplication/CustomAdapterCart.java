package com.example.finalapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomAdapterCart extends SimpleAdapter {

    private Context mContext;
    public LayoutInflater inflater=null;
    public CustomAdapterCart(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        try{
            if(convertView==null)
                vi = inflater.inflate(R.layout.user_cart_list, null);
            HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
            TextView tvfoodname = vi.findViewById(R.id.textView);
            TextView tvfoodprice = vi.findViewById(R.id.textView2);
            TextView tvquantity = vi.findViewById(R.id.textView3);
            TextView tvstatus = vi.findViewById(R.id.textView4);
            CircleImageView imgfood =vi.findViewById(R.id.imageView2);
            String diname = (String) data.get("Itemname");//hilang
            String ditemprice =(String) data.get("Itemprice");
            String ditemquan =(String) data.get("Quantity");
            String diid=(String) data.get("Itemid");
            String dist=(String) data.get("Status");
            String diorderid=(String) data.get("Orderid");
            tvfoodname.setText(diname);
            tvfoodprice.setText(ditemprice);
            tvquantity.setText(ditemquan);
            tvstatus.setText(dist);
            String image_url = "http://socstudents.net/yourwaystechnology/itemimages/"+diid+".jpg";
            Picasso.with(mContext).load(image_url)
                    .fit().into(imgfood);
//                    .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)

        }catch (IndexOutOfBoundsException e){

        }

        return vi;
    }
}