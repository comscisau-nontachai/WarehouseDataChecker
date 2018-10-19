package dev.strubber.com.warehousedata;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import dev.strubber.com.warehousedata.fragment.ShowListWH01Fragment;
import dev.strubber.com.warehousedata.model.ConnectionDB;

public class DetailSearchActivity extends AppCompatActivity {

    private ConnectionDB connectionDB = new ConnectionDB();

    private String part_id,type_id,record_id,image;

    private TextView txt_part, txt_relate, txt_group, txt_detail, txt_area, txt_qty, txt_type;
    private EditText edt_quantity_new;
    private ImageView image_product;
    private ProgressBar progressBar;
    private LinearLayout lineawr_detail;

   private String part, relate, group, qty, qty_real, detail, area1, area2, area3, area4, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ///receive data
        part_id = getIntent().getStringExtra("part_id");
        type_id = getIntent().getStringExtra("type_id");
        record_id = getIntent().getStringExtra("record_id");
        image = getIntent().getStringExtra("image");

        txt_part = findViewById(R.id.txt_part);
        txt_relate = findViewById(R.id.txt_relate);
        txt_group = findViewById(R.id.txt_group);
        txt_detail = findViewById(R.id.txt_detail);
        txt_area = findViewById(R.id.txt_area_all);
        txt_qty = findViewById(R.id.txt_qty);
        txt_type = findViewById(R.id.txt_type);
        edt_quantity_new = findViewById(R.id.edt_real_count);
        image_product = findViewById(R.id.image_detail);
        progressBar = findViewById(R.id.progress_bar);
        lineawr_detail = findViewById(R.id.linear_detail_product);

        ///get data
        new GetDetail().execute();

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new InsertData().execute();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class GetDetail extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Connection conn = connectionDB.CONN("HR_management");
            if (conn != null) {
                try {


                    String query = "select v_pro.id,v_pro.part,v_pro.relate,pn.description,ld3.name,pt.type_name,st.st_name,std.detail_name,v_pro.area_name,v_pro.row_name,v_pro.column_no,v_pro.shelf,FORMAT(v_pro.qty,'N0')as qty2,FORMAT(cp.count_new,'0')as count_new from Sys_WarehouseManagement..v_Products_Description v_pro \n" +
                            "LEFT JOIN [ST_TEST].[dbo].[mob_check_partnumber] cp ON v_pro.part_id = cp.part_id_checked and v_pro.type_id = cp.type_id_check and v_pro.id=cp.record_part\n" +
                            "INNER JOIN [Sys_DataMaster].[dbo].[sys_partnumber_group] pg ON v_pro.part_id = pg.part_id\n" +
                            "INNER JOIN [Sys_DataMaster].[dbo].[layer3_data] ld3 ON pg.type_id = ld3.id\n" +
                            "INNER JOIN [Sys_DataMaster].[dbo].[sys_partnumber_type] spnt ON v_pro.part_id = spnt.part_id\n" +
                            "INNER JOIN  [Sys_DataMaster].[dbo].[partnumber_type] pt ON spnt.type_part = pt.record_id\n" +
                            "INNER JOIN [Sys_DataMaster].[dbo].[st_type] st ON spnt.work_type = st.record_id\n" +
                            "INNER JOIN [Sys_DataMaster].[dbo].[st_type_detail] std ON spnt.detail_type = std.record_id\n" +
                            "INNER JOIN [Sys_DataMaster].[dbo].[sys_partnumber_new] pn ON pn.record_id = v_pro.part_id\n" +
                            "where  v_pro.part_id = "+part_id+" and v_pro.type_id="+type_id+" and v_pro.id="+record_id+" and v_pro.IsDelete=0 and pg.IsDelete=0 and ld3.IsDelete=0 and spnt.IsDelete=0\n" +
                            "and pt.IsDelete=0 and st.IsDelete=0 and std.IsDelete=0 and v_pro.IsDelete=0";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);


                    while (rs.next()) {

                        part = rs.getString("part");
                        relate = rs.getString("relate");
                        group = rs.getString("name");
                        detail = rs.getString("description");
                        type = rs.getString("type_name") + "/" + rs.getString("st_name") + "/" + rs.getString("detail_name");
                        area1 = rs.getString("area_name");
                        area2 = rs.getString("row_name");
                        area3 = rs.getString("column_no");
                        area4 = rs.getString("shelf");
                        qty = rs.getString("qty2");
                        qty_real = rs.getString("count_new");


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

            progressBar.setVisibility(View.GONE);
            lineawr_detail.setVisibility(View.VISIBLE);

            if (type_id.equals("1")) {
                txt_part.setText(part + "JAP");
            } else if (type_id.equals("2")) {
                txt_part.setText(part + "HIC");
            } else {
                txt_part.setText(part);
            }
            Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/MavenPro-Regular.ttf");
            txt_part.setTypeface(tf, Typeface.BOLD);

            txt_relate.setText("พาร์ทนัมเบอร์ที่เกี่ยวข้อง : " + relate);
            txt_detail.setText("รายละเอียดสินค้า : " + detail);
            txt_group.setText("กลุ่มสินค้า : " + group);
            txt_type.setText("ประเภทสินค้า : " + type);
            txt_area.setText(area1 + " / " + area2 + " / " + area3 + " / " + area4);

            if (qty_real != null) {
                txt_qty.setText(qty_real);
                edt_quantity_new.setText(String.valueOf(qty_real));
            } else {
                txt_qty.setText(qty);
                edt_quantity_new.setText(String.valueOf(qty));
            }


            //set image
            String img = "http://roomdatasoftware.strubberdata.com/pic_partnumber_new/" + image;
            Picasso.get().load(img).placeholder(R.drawable.no_image).into(image_product);

            super.onPostExecute(aVoid);
        }
    }

    private class InsertData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Connection conn = connectionDB.CONN("HR_management");
            if (conn != null) {
                try {

                    String query = "IF not EXISTS(select * from Sys_WarehouseManagement..v_Products_Description v_pro LEFT JOIN ST_TEST..mob_check_partnumber cp\n" +
                            "    ON v_pro.part_id = cp.part_id_checked and record_part = "+record_id+" WHERE part_id_checked = "+part_id+" and type_id_check = "+type_id+" and v_pro.id="+record_id+")\n" +
                            "            BEGIN\n" +
                            "                 insert into ST_TEST..mob_check_partnumber values("+record_id+",'"+part+"',"+part_id+","+type_id+","+qty+","+edt_quantity_new.getText().toString()+",GETDATE(),'"+MainActivity.name+"');\n" +
                            "            END\n" +
                            "            ELSE\n" +
                            "            BEGIN\n" +
                            "              update ST_TEST..mob_check_partnumber set count_new = "+edt_quantity_new.getText().toString()+",date_check=GETDATE(),name_check='"+edt_quantity_new.getText().toString()+"' where part_id_checked = "+part_id+" and type_id_check = "+type_id+" and record_part = "+record_id+"\n" +
                            "            END";
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);


                    while (rs.next()) {
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

           finish();

            super.onPostExecute(aVoid);
        }
    }
}
