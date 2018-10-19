package dev.strubber.com.warehousedata;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.strubber.com.warehousedata.model.ConnectionDB;
import dev.strubber.com.warehousedata.model.WareHouseModel;

public class MainActivity extends AppCompatActivity{

    ///test
    private ConnectionDB connectionDB = new ConnectionDB();
    SearchView searchView;
    RecyclerView recycler_view;
    List<WareHouseModel> list;

    //Volley
    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    private String URL = "";

    ///Get Image Person
    private String URL_IMAGE="";
    private String image;
    private ImageView image_v_person;
    public static String u_login_personnal, u_login_id;

    private TextView txt_name;
    public static String name;
    private LinearLayout linear_warehouse;


    @Override
    protected void onResume() {
        super.onResume();

        searchView.setActivated(true);
        searchView.setFocusable(false);
        searchView.onActionViewExpanded();
        searchView.setIconified(false);
        searchView.clearFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        image_v_person = findViewById(R.id.image_v);
        txt_name = findViewById(R.id.txt_name);
        linear_warehouse = findViewById(R.id.linear_warehouse);

        ///Search VIew
        searchView = findViewById(R.id.search);
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(newText.length() > 1)
                {
                    ///execute
                    list.clear();
                    recycler_view.setVisibility(View.VISIBLE);
                    URL = "http://roomdatasoftware.strubberdata.com/pic_partnumber_new/get_list_part_by_search.php?part="+newText;
                    callVolley();

                    //hide warehouse layout
                    linear_warehouse.setVisibility(View.GONE);
                }
                if(newText.length() == 0)
                {
                    recycler_view.setVisibility(View.GONE);
                    linear_warehouse.setVisibility(View.VISIBLE);
                }


                return false;
            }
        });
        ImageView closeButton = (ImageView) this.searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchView.setQuery("", false);
                recycler_view.setVisibility(View.GONE);

                linear_warehouse.setVisibility(View.VISIBLE);
            }
        });


        ////set name and image
        u_login_id = getIntent().getStringExtra("LOGIN_ID");
        u_login_personnal = getIntent().getStringExtra("LOGIN_PERSONAL");

        ///get user data
        new GetUserData().execute();

        ///exit
        findViewById(R.id.img_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ////btn go to warehouse
        findViewById(R.id.card_view_wh01).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),WH01Activity.class));
            }
        });

    }

    private void callVolley() {


        request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;
                for (int i=0; i<response.length();i++)
                {
                    String part,relate,qty,area1,area2,area3,area4,part_id,type_id,count_new,image,date_check,record_id;
                    try {

                        jsonObject = response.getJSONObject(i);

                        part = jsonObject.getString("part");
                        relate = jsonObject.getString("relate");
                        qty = jsonObject.getString("qty");
                        area1 = jsonObject.getString("area_name");
                        area2 = jsonObject.getString("row_name");
                        area3 = jsonObject.getString("column_no");
                        area4 = jsonObject.getString("shelf");
                        part_id = jsonObject.getString("part_id");
                        type_id = jsonObject.getString("type_id");
                        count_new = jsonObject.getString("count_new");
                        image = jsonObject.getString("image");
                        date_check = jsonObject.getString("date_check");
                        record_id = jsonObject.getString("record_id");
//                        date_check = "2018/11/15";

                        WareHouseModel data = new WareHouseModel(part,relate,qty,area1,area2,area3,area4,part_id,type_id,count_new,image,date_check,record_id);
                        list.add(data);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                setAdapter();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });


        requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private void setAdapter() {
        ProductAdapter productAdapter = new ProductAdapter(getApplicationContext(),list);
        recycler_view.setAdapter(productAdapter);
    }

    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{

        Context context;
        List<WareHouseModel> list;

        public ProductAdapter(Context context, List list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_recycler_showlist,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


            String part_and_option;
            if(list.get(position).type_id.equals("1"))
            {
                part_and_option = list.get(position).part+"JAP";
            }else if(list.get(position).type_id.equals("2"))
            {
                part_and_option = list.get(position).part+"HIC";
            }else if(list.get(position).type_id.equals("13"))
            {
                part_and_option = list.get(position).part+"";
            }
            else if(list.get(position).type_id.equals("15"))
            {
                part_and_option = list.get(position).part+"STL";
            }else
            {
                part_and_option = list.get(position).part+"ORI";
            }
            holder.txt_part.setText(part_and_option);
            holder.txt_relate.setText(list.get(position).relate);
            holder.txt_area_all.setText(list.get(position).area1+" / "+list.get(position).area2+" / "+list.get(position).area3+" / "+list.get(position).area4);

            holder.linear_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(context, list.get(position).part, Toast.LENGTH_SHORT).show();

                    searchView.setQuery("", false);
                    recycler_view.setVisibility(View.GONE);

                    Intent intent = new Intent(getApplicationContext(),DetailSearchActivity.class);
                    intent.putExtra("part_id",list.get(position).part_id);
                    intent.putExtra("type_id",list.get(position).type_id);
                    intent.putExtra("record_id",list.get(position).record_id);
                    intent.putExtra("image",list.get(position).image);
                    startActivity(intent);

                }
            });

            if(!list.get(position).count_new.equals("null"))
            {
                ///checked
                holder.img_checked.setVisibility(View.VISIBLE);
                holder.txt_quantity.setText(list.get(position).count_new);
                holder.txt_date_check.setText(convertDateFormat(list.get(position).date_check));
                holder.txt_date_check.setVisibility(View.VISIBLE);
            }else
            {
                /// not checked
                holder.img_checked.setVisibility(View.GONE);
                holder.txt_quantity.setText(list.get(position).quantity);
                holder.txt_date_check.setVisibility(View.GONE);
            }


            ////set image recycler view
            String img = "http://roomdatasoftware.strubberdata.com/pic_partnumber_new/"+list.get(position).image;
            Picasso.get().load(img).placeholder(R.drawable.no_image).into(holder.img_product);

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txt_part,txt_relate,txt_quantity,txt_area_all,txt_date_check;
            ImageView img_product,img_checked;
            LinearLayout linear_holder;
            public ViewHolder(View itemView) {
                super(itemView);

                txt_part = itemView.findViewById(R.id.txt_part);
                txt_relate = itemView.findViewById(R.id.txt_relate);
                txt_quantity = itemView.findViewById(R.id.txt_quantity);
                txt_area_all = itemView.findViewById(R.id.txt_area_all);
                txt_date_check = itemView.findViewById(R.id.txt_date_checked);

                img_product = itemView.findViewById(R.id.img_product);
                img_checked = itemView.findViewById(R.id.img_checked);

                linear_holder = itemView.findViewById(R.id.linear_list_item);

            }
        }
    }

    private String convertDateFormat(String date)
    {
        SimpleDateFormat old_format = new SimpleDateFormat("yyyy/MM/dd");

        Date dateObj = null;
        try {
            dateObj = old_format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat new_format = new SimpleDateFormat("dd MMMM yyyy");
        String new_date = new_format.format(dateObj);
        return new_date;
    }

    private class GetUserData extends AsyncTask<Void, Void, Void> {
        String personnel_no;
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            Connection conn = connectionDB.CONN("HR_management");
            if (conn != null) {
                try {

                    String query = "select * from HR_management..pn_datapersonnel where login_id='"+u_login_id+"' or personnel_no='"+u_login_personnal+"' and delete_bit=0";

                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {
                        personnel_no = rs.getString("personnel_no");
                        name = rs.getString("name");
                    }
                    conn.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "can't connect to server !!, please contact admin.", Toast.LENGTH_SHORT).show();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            //get image profile
            URL_IMAGE = "http://hrmsoftware.strubberdata.com/personnel_img/getImagePersonal.php?id=" + personnel_no;
            getImagePerson();
            txt_name.setText(name);

            super.onPostExecute(aVoid);
        }
    }
    private void getImagePerson() {

        JsonArrayRequest request = new JsonArrayRequest(URL_IMAGE, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        image = "http://hrmsoftware.strubberdata.com/personnel_img/"+ jsonObject.getString("image");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Picasso.get().load(image).error(R.drawable.no_image)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .centerCrop()
                        .fit()
                        .into(image_v_person);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

}