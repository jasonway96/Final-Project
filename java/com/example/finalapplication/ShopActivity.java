package com.example.finalapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopActivity extends AppCompatActivity {
    TextView tvrname,tvrphone,tvrloc;
    ImageView imgRest;
    ListView lvitem;
    Dialog myDialogWindow;
    ArrayList<HashMap<String, String>> itemlist;
    String userid,shopid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //Log.e("MICKY",(bundle.getString("Location")));
        shopid = bundle.getString("Shopid");
        String sname = bundle.getString("Name");
        String sphone = bundle.getString("Phone");
        String slocation = bundle.getString("Location");
        userid = bundle.getString("Email");
        initView();
        tvrname.setText(sname);
        //tvraddress.setText(raddress);
        tvrphone.setText(sphone);
        tvrloc.setText(slocation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Picasso.with(this).load("http://socstudents.net/yourwaystechnology/images/"+shopid+".jpg")
                .fit().into(imgRest);
        //  .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)

        lvitem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showItemDetail(position);
            }
        });
        loadItem(shopid);

    }

    private void showItemDetail(int p) {
        myDialogWindow = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);//Theme_DeviceDefault_Dialog_NoActionBar
        myDialogWindow.setContentView(R.layout.dialog_window);
        myDialogWindow.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView tviname,tviprice,tviquan;
        ImageView imgitem = myDialogWindow.findViewById(R.id.imageViewFood);
        final Spinner spquan = myDialogWindow.findViewById(R.id.spinner2);
        Button btnorder = myDialogWindow.findViewById(R.id.button2);
        tviname= myDialogWindow.findViewById(R.id.textView12);
        tviprice = myDialogWindow.findViewById(R.id.textView13);
        tviquan = myDialogWindow.findViewById(R.id.textView14);
        tviname.setText(itemlist.get(p).get("Itemname"));
        tviprice.setText(itemlist.get(p).get("Itemprice"));
        tviquan.setText(itemlist.get(p).get("Itemquantity"));
        final String itemid =(itemlist.get(p).get("Itemid"));
        final String itemname = itemlist.get(p).get("Itemname");
        final String itemprice = itemlist.get(p).get("Itemprice");
        btnorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String iquan = spquan.getSelectedItem().toString();
                dialogOrder(itemid,itemname,iquan,itemprice);
            }
        });
        int quan = Integer.parseInt(itemlist.get(p).get("Itemquantity"));
        List<String> list = new ArrayList<String>();
        for (int i = 1; i<=quan;i++){
            list.add(""+i);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spquan.setAdapter(dataAdapter);

        Picasso.with(this).load("http://socstudents.net/yourwaystechnology/itemimages/"+itemid+".jpg")
                .memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE)
                .fit().into(imgitem);
        myDialogWindow.show();
    }

    private void dialogOrder(final String itemid, final String itemname, final String iquan, final String itemprice) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Order "+itemname+ " with quantity "+iquan);

        alertDialogBuilder
                .setMessage("Are you sure")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        insertCart(itemid,itemname,iquan,itemprice);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void insertCart(final String itemid, final String itemname, final String iquan, final String itemprice) {
        class InsertCart extends AsyncTask<Void,Void,String>{

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("Itemid",itemid);
                hashMap.put("Shopid",shopid);
                hashMap.put("Itemname",itemname);
                hashMap.put("Quantity",iquan);
                hashMap.put("Itemprice",itemprice);
                hashMap.put("Email",userid);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://socstudents.net/yourwaystechnology/insert_cart.php",hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                Log.e("LOL",s);
                super.onPostExecute(s);
                if (s.equalsIgnoreCase("Success")){
                    Toast.makeText(ShopActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    myDialogWindow.dismiss();
                    loadItem(shopid);
                }else{
                    Toast.makeText(ShopActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }

        }
        InsertCart insertCart = new InsertCart();
        insertCart.execute();
    }

    private void loadItem(final String Shopid) {
        class LoadItem extends AsyncTask<Void,Void,String>{

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("Shopid",Shopid);
                RequestHandler requestHandler = new RequestHandler();
                String s = requestHandler.sendPostRequest("http://socstudents.net/yourwaystechnology/load_item.php",hashMap);
                return s;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                itemlist.clear();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray Itemarray = jsonObject.getJSONArray("Item");
                    for (int i = 0; i < Itemarray.length(); i++) {
                        JSONObject c = Itemarray.getJSONObject(i);
                        String jsid = c.getString("Itemid");
                        String jsfname = c.getString("Itemname");
                        String jsfprice = c.getString("Itemprice");
                        String jsquan = c.getString("Quantity");
                        HashMap<String,String> itemlisthash = new HashMap<>();
                        itemlisthash.put("Itemid",jsid);
                        itemlisthash.put("Itemname",jsfname);
                        itemlisthash.put("Itemprice",jsfprice);
                        itemlisthash.put("Itemquantity",jsquan);
                        itemlist.add(itemlisthash);
                    }
                }catch(JSONException e){}
                ListAdapter adapter = new CustomAdapterItem(
                        ShopActivity.this, itemlist,
                        R.layout.item_list_rest, new String[]
                        {"Itemname","Itemprice","Itemquantity"}, new int[]
                        {R.id.textView,R.id.textView2,R.id.textView3});
                lvitem.setAdapter(adapter);

            }
        }
        LoadItem loadItem = new LoadItem();
        loadItem.execute();
    }

    private void initView() {
        imgRest = findViewById(R.id.imageView3);
        tvrname = findViewById(R.id.textView6);
        tvrphone = findViewById(R.id.textView7);
        tvrloc = findViewById(R.id.textView9);
        lvitem = findViewById(R.id.listviewfood);
        itemlist = new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ShopActivity.this,MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Email",userid);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
