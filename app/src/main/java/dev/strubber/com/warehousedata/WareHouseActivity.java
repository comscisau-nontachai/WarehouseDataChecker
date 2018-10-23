package dev.strubber.com.warehousedata;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dev.strubber.com.warehousedata.fragment.DetailWh01Fragment;
import dev.strubber.com.warehousedata.fragment.ShowListWH01Fragment;
import dev.strubber.com.warehousedata.model.ConnectionDB;
import dev.strubber.com.warehousedata.model.WareHouseModel;

public class WareHouseActivity extends AppCompatActivity {

    private ConnectionDB connectionDB = new ConnectionDB();

    private RecyclerView recyclerView;
    private List<WareHouseModel> list_category;
    public static String category_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warehouse);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        category_id = getIntent().getStringExtra("CATEGORY_ID");
        switch (category_id) {
            case "31": {
                getSupportActionBar().setTitle("ข้อมูล คลังสินค้าสำเร็จรูป");
                getFirstPageFragmnet("WH01");
                break;
            }
            case "32": {
                getSupportActionBar().setTitle("ข้อมูล คลังวัตถุดิบ");
                getFirstPageFragmnet("RM01");
                break;
            }
            default: {
                getSupportActionBar().setTitle("ข้อมูล คลังส่วนประกอบ");
                getFirstPageFragmnet("RM06");
            }
        }

        list_category = new ArrayList<>();
        new CategoryData(category_id).execute();


    }

    private void getFirstPageFragmnet(String warehouse_id)
    {
        //////call product list in first come
        Bundle bundle = new Bundle();
        ShowListWH01Fragment fragment = new ShowListWH01Fragment();

        bundle.putString("wh_no", warehouse_id);//WH01
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_contianer, fragment).commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
        private int selectedPosition = -1;
        Context context;
        List<WareHouseModel> list;

        public CategoryAdapter(Context context, List list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_recycler_category, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            holder.txtname.setText(list.get(position).warehouse_no + " " + list.get(position).warehouse_name);

            //Toast.makeText(context, list.get(position).warehouse_name + "||" + list.get(position).warehouse_no, Toast.LENGTH_SHORT).show();

            if (selectedPosition == position) {
                holder.linear_category.setSelected(true); //using selector drawable
                holder.linear_category.setBackgroundColor(getResources().getColor(R.color.left_menu_color_active));
                holder.view_left_menu.setVisibility(View.VISIBLE);
            } else {
                holder.linear_category.setSelected(false);
                holder.linear_category.setBackgroundColor(getResources().getColor(R.color.left_menu_color));//default
                holder.view_left_menu.setVisibility(View.GONE);
            }
            holder.linear_category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedPosition >= 0)
                        notifyItemChanged(selectedPosition);
                    selectedPosition = holder.getAdapterPosition();
                    notifyItemChanged(selectedPosition);

                    ////call new fragment
                    Bundle bundle = new Bundle();
                    ShowListWH01Fragment fragment = new ShowListWH01Fragment();
                    bundle.putString("wh_no", list.get(position).warehouse_no);
                    fragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.detail_contianer, fragment).commit();


                    ///set adapter position to 0 on click change
                    DetailWh01Fragment.adapter_position="0";

                }
            });


        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtname;
            LinearLayout linear_category;
            View view_left_menu;

            public ViewHolder(View itemView) {
                super(itemView);

                txtname = itemView.findViewById(R.id.txt_name);
                linear_category = itemView.findViewById(R.id.linear_category);
                view_left_menu = itemView.findViewById(R.id.incicate_left);

            }
        }
    }

    private class CategoryData extends AsyncTask<Void, Void, Void> {

        String category_id;

        public CategoryData(String category_id) {
            this.category_id = category_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Connection conn = connectionDB.CONN("HR_management");
            if (conn != null) {
                try {

                    String query = "select * from Sys_WarehouseManagement..sys_warehouse where IsDelete=0 and category_id='" + category_id + "'";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {


                        String ware_name, ware_no;
                        ware_name = rs.getString("warehouse_name");
                        ware_no = rs.getString("warehouse_no");

                        WareHouseModel data = new WareHouseModel(ware_name, ware_no);
                        list_category.add(data);

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

            CategoryAdapter adapter = new CategoryAdapter(getApplicationContext(), list_category);
            recyclerView.setAdapter(adapter);

            super.onPostExecute(aVoid);
        }
    }
}
