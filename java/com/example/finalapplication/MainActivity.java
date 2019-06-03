package com.example.finalapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    ListView lvshop;
    ArrayList<HashMap<String, String>> itemlist;
    ArrayList<HashMap<String, String>> cartlist;
    ArrayList<HashMap<String, String>> Shoplist;
    double total;
    Spinner sploc;
    String userid,name,phone;
    Dialog myDialogCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvshop = findViewById(R.id.listviewShop);
        sploc = findViewById(R.id.spinner);
        cartlist = new ArrayList<>();
        itemlist = new ArrayList<>();
        Shoplist = new ArrayList<>();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Log.e("MICKY",(bundle.getString("Phone")));
        userid = bundle.getString("Email");
        name = bundle.getString("Name");
        phone = bundle.getString("Phone");
        loadShop(sploc.getSelectedItem().toString());
        lvshop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, restlist.get(position).get("restid"), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,ShopActivity.class);
                Bundle bundle = new Bundle();
                Log.e("HANIS",Shoplist.get(position).get("Shopid"));
                bundle.putString("Shopid",Shoplist.get(position).get("Shopid"));
                bundle.putString("Name",Shoplist.get(position).get("Name"));
                bundle.putString("Location",Shoplist.get(position).get("Location"));
                bundle.putString("Phone",Shoplist.get(position).get("Phone"));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        sploc.setSelection(0,false);
        sploc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadShop(sploc.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }



    private void loadShop(final String loc) {
        class LoadShop extends AsyncTask<Void,Void,String>{

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("Location",loc);
                RequestHandler rh = new RequestHandler();
                Shoplist = new ArrayList<>();
                String s = rh.sendPostRequest
                        ("http://socstudents.net/yourwaystechnology/load_shop.php",hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                // Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                Shoplist.clear();
                try{
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray Shoparray = jsonObject.getJSONArray("Shop");
                    Log.e("Jason",jsonObject.toString());
                    for (int i=0;i<Shoparray.length();i++){
                        JSONObject c = Shoparray.getJSONObject(i);
                        String sid = c.getString("Shopid");
                        String sname = c.getString("Name");
                        String sphone = c.getString("Phone");
                        String slocation = c.getString("Location");
                        HashMap<String,String> shoplisthash = new HashMap<>();
                        shoplisthash.put("Shopid",sid);
                        shoplisthash.put("Name",sname);
                        shoplisthash.put("Phone",sphone);
                        shoplisthash.put("Location",slocation);
                        Shoplist.add(shoplisthash);
                    }
                }catch (final JSONException e){
                    Log.e("JSONERROR",e.toString());
                }

                ListAdapter adapter = new CustomAdapter(
                        MainActivity.this, Shoplist,
                        R.layout.cust_list_rest, new String[]
                        {"Name","Phone","Location","Shopid"}, new int[]
                        {R.id.textView,R.id.textView2,R.id.textView3,R.id.textView4});
                lvshop.setAdapter(adapter);
            }

        }
        LoadShop loadShop = new LoadShop();
        loadShop.execute();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.mycart:
                loadCartData();
                return true;
            case R.id.myprofile:
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Email",userid);
                bundle.putString("Name",name);
                bundle.putString("Phone",phone);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadCartWindow() {
        myDialogCart = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);//Theme_DeviceDefault_Dialog_NoActionBar
        myDialogCart.setContentView(R.layout.cart_window);
        myDialogCart.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ListView lvcart = myDialogCart.findViewById(R.id.lvmycart);
        TextView tvtotal = myDialogCart.findViewById(R.id.textViewTotal);
        TextView tvorderid = myDialogCart.findViewById(R.id.textOrderId);
        Button btnpay = myDialogCart.findViewById(R.id.btnPay);
        Log.e("HANIS","SIZE:"+cartlist.size());
        lvcart.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dialogDeleteFood(position);
                return false;
            }
        });
        btnpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPay();
            }
        });
        ListAdapter adapter = new CustomAdapterCart(
                MainActivity.this, cartlist,
                R.layout.user_cart_list, new String[]
                {"Itemname","Itemprice","Quantity","Status"}, new int[]
                {R.id.textView,R.id.textView2,R.id.textView3,R.id.textView4});
        lvcart.setAdapter(adapter);
        tvtotal.setText("RM "+total);
        tvorderid.setText(cartlist.get(0).get("Orderid"));
        myDialogCart.show();

    }

    private void dialogDeleteFood(final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Delete Item "+cartlist.get(position).get("Itemname")+"?");
        alertDialogBuilder
                .setMessage("Are you sure")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // Toast.makeText(MainActivity.this, cartlist.get(position).get("foodname"), Toast.LENGTH_SHORT).show();
                        deleteCartFood(position);
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

    private void deleteCartFood(final int position) {
        class DeleteCartFood extends AsyncTask<Void,Void,String>{

            @Override
            protected String doInBackground(Void... voids) {
                String itemid = cartlist.get(position).get("Itemid");
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("Itemid",itemid);
                hashMap.put("Email",userid);
                RequestHandler requestHandler = new RequestHandler();
                String s = requestHandler.sendPostRequest("http://socstudents.net/yourwaystechnology/php/delete_cart.php",hashMap);
                return s;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s.equalsIgnoreCase("success")){
                    myDialogCart.dismiss();
                    loadCartData();
                    Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        DeleteCartFood deleteCartFood = new DeleteCartFood();
        deleteCartFood.execute();
    }

    private void dialogPay() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Proceed with payment?");

        alertDialogBuilder
                .setMessage("Are you sure")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Intent intent = new Intent(MainActivity.this,PaymentActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("Email",userid);
                        bundle.putString("Name",name);
                        bundle.putString("Phone",phone);
                        bundle.putString("Total", String.valueOf(total));
                        bundle.putString("Orderid", cartlist.get(0).get("Orderid"));
                        //intent.putExtras(bundle);
                        myDialogCart.dismiss();
                        //startActivity(intent);
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
    private void loadCartData() {
        class LoadCartData extends AsyncTask<Void,Void,String>{

            @Override
            protected String doInBackground(Void... voids) {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("Email",userid);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest("http://socstudents.net/yourwaystechnology/php/load_cart.php",hashMap);
                return s;
            }

            @Override
            protected void onPostExecute(String s) {
                Log.e("LOL",s);
                cartlist.clear();
                total = 0;
                try{
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray cartarray = jsonObject.getJSONArray("Cart");

                    for (int i=0;i<cartarray .length();i++) {
                        JSONObject c = cartarray .getJSONObject(i);
                        String jfid = c.getString("Itemid");
                        String jfn = c.getString("Itemname");
                        String jfp = c.getString("Itemprice");
                        String jfq = c.getString("Quantity");
                        String jst = c.getString("Status");
                        String joid = c.getString("Orderid");
                        HashMap<String,String> cartlisthash = new HashMap<>();
                        cartlisthash .put("Itemid",jfid);
                        cartlisthash .put("Itemname",jfn);
                        cartlisthash .put("Itemprice","RM "+jfp);
                        cartlisthash .put("Quantity",jfq+" set");
                        cartlisthash .put("Status",jst);
                        cartlisthash .put("Orderid",joid);
                        cartlist.add(cartlisthash);
                        total = total + (Double.parseDouble(jfp) * Double.parseDouble(jfq));
                    }
                }catch (JSONException e){}
                super.onPostExecute(s);
                if (total>0){
                    loadCartWindow();
                }else{
                    Toast.makeText(MainActivity.this, "Cart is feeling empty", Toast.LENGTH_SHORT).show();
                }

            }
        }
        LoadCartData loadCartData = new LoadCartData();
        loadCartData.execute();
    }
}
