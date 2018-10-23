package dev.strubber.com.warehousedata.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.strubber.com.warehousedata.MainActivity;
import dev.strubber.com.warehousedata.R;
import dev.strubber.com.warehousedata.WareHouseActivity;
import dev.strubber.com.warehousedata.model.WareHouseModel;
import dev.strubber.com.warehousedata.model.ConnectionDB;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowListWH01Fragment extends Fragment {

    private ConnectionDB connectionDB = new ConnectionDB();

    private RecyclerView recyclerview_product;
    List<WareHouseModel> list_product;

    private String wh_no;
    private ProgressBar progressBar;

    //Volley
    private JsonArrayRequest request;
    private RequestQueue requestQueue;
    private String URL = "";

    //define pos adapter at first
    private int adapt_pos;


    public ShowListWH01Fragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_list_wh01, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        recyclerview_product = view.findViewById(R.id.recycler_view_product);
        recyclerview_product.setHasFixedSize(true);
        recyclerview_product.setLayoutManager(new LinearLayoutManager(getActivity()));

        list_product = new ArrayList<>();

        wh_no = getArguments().getString("wh_no");
        callVolley();


        ///set Header on Visible
        View view_test = getActivity().findViewById(R.id.linear_header);
        view_test.setVisibility(View.VISIBLE);

    }

    private void callVolley() {
        URL = "http://roomdatasoftware.strubberdata.com/pic_partnumber_new/get_list_part_by_wh.php?wh=" + wh_no;

        progressBar.setVisibility(View.VISIBLE);
        request = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressBar.setVisibility(View.GONE);

                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    String part, relate, qty, area1, area2, area3, area4, part_id, type_id, count_new, image, date_check, record_id,warehouse_no;
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
                        warehouse_no = "dummy_data";

                        WareHouseModel data = new WareHouseModel(part,relate,qty,area1,area2,area3,area4,part_id,type_id,count_new,image,date_check,record_id,warehouse_no);
                        list_product.add(data);
                    } catch (JSONException e) {
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


        requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(request);


    }

    private void setAdapter() {
        ProductAdapter productAdapter = new ProductAdapter(getActivity(), list_product);

        adapt_pos = Integer.parseInt(DetailWh01Fragment.adapter_position!=null?DetailWh01Fragment.adapter_position:"0");
        recyclerview_product.getLayoutManager().scrollToPosition(adapt_pos);

        recyclerview_product.setAdapter(productAdapter);

    }


    public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

        Context context;
        List<WareHouseModel> list;

        public ProductAdapter(Context context, List list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_recycler_showlist, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

            String part_type="";
            ////Check warehouse for extend.
            if (WareHouseActivity.category_id.equals("31")) {

                if (list.get(position).type_id.equals("1")) {
                    part_type = "JAP";
                } else if (list.get(position).type_id.equals("2")) {
                    part_type = "HIC";
                } else if (list.get(position).type_id.equals("13")) {
                    part_type = "";
                } else if (list.get(position).type_id.equals("15")) {
                    part_type = "STL";
                } else {
                    part_type = "ORI";
                }

            } else if (WareHouseActivity.category_id.equals("32")) {

                if (list.get(position).type_id.equals("1")) {
                    part_type = "";
                }else if(list.get(position).type_id.equals("2"))
                {
                    part_type = "YSS";
                }else
                {
                    part_type = "KYB";
                }
            } else {
            }

            holder.txt_part.setText(list.get(position).part+part_type);
//            holder.txt_relate.setText(list.get(position).relate);


            String area_sum = !list.get(position).area1.equals("null")?list.get(position).area1:"-";
            area_sum += " / ";
            area_sum += !list.get(position).area2.equals("null")?list.get(position).area2:"-";
            area_sum += " / ";
            area_sum += !list.get(position).area3.equals("null")?list.get(position).area3:"-";
            area_sum += " / ";
            area_sum += !list.get(position).area4.equals("0")?list.get(position).area4:"-";
            holder.txt_area_all.setText(area_sum);



            holder.linear_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    DetailWh01Fragment fragment = new DetailWh01Fragment();
                    bundle.putString("wh_no_detail", wh_no); //send bundle for back current category
                    bundle.putString("part_id", list.get(position).part_id);
                    bundle.putString("type_id", list.get(position).type_id);
                    bundle.putString("record_id", list.get(position).record_id);
                    bundle.putString("image", list.get(position).image);
                    ///test
                    bundle.putString("adapter_position", String.valueOf(position));

                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.detail_contianer, fragment).commit();


                }
            });

            if (!list.get(position).count_new.equals("null")) {
                ///checked
                holder.img_checked.setVisibility(View.VISIBLE);
                holder.txt_quantity.setText(list.get(position).count_new);
                holder.txt_date_check.setText(convertDateFormat(list.get(position).date_check));
                holder.txt_date_check.setVisibility(View.VISIBLE);
            } else {
                /// not checked
                holder.img_checked.setVisibility(View.GONE);
                holder.txt_quantity.setText(list.get(position).quantity);
                holder.txt_date_check.setVisibility(View.GONE);
            }


            ////set image recycler view
            String img = "http://roomdatasoftware.strubberdata.com/pic_partnumber_new/" + list.get(position).image;
            Picasso.get().load(img).placeholder(R.drawable.no_image).into(holder.img_product);

            //hide warehouse name
            holder.txt_warehouse.setVisibility(View.GONE);

        }

        @Override
        public int getItemCount() {
            return list_product.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txt_part, txt_relate, txt_quantity, txt_area_all, txt_date_check,txt_warehouse;
            ImageView img_product, img_checked;
            LinearLayout linear_holder;


            public ViewHolder(View itemView) {
                super(itemView);

                txt_part = itemView.findViewById(R.id.txt_part);
                txt_relate = itemView.findViewById(R.id.txt_relate);
                txt_quantity = itemView.findViewById(R.id.txt_quantity);
                txt_area_all = itemView.findViewById(R.id.txt_area_all);
                txt_date_check = itemView.findViewById(R.id.txt_date_checked);
                txt_warehouse = itemView.findViewById(R.id.txt_warehouse);

                img_product = itemView.findViewById(R.id.img_product);
                img_checked = itemView.findViewById(R.id.img_checked);

                linear_holder = itemView.findViewById(R.id.linear_list_item);

            }
        }
    }

    private String convertDateFormat(String date) {
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


}
