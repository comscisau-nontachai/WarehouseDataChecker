package dev.strubber.com.warehousedata.fragment;


import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import dev.strubber.com.warehousedata.MainActivity;
import dev.strubber.com.warehousedata.R;
import dev.strubber.com.warehousedata.WareHouseActivity;
import dev.strubber.com.warehousedata.model.ConnectionDB;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailWh01Fragment extends Fragment {

    private ConnectionDB connectionDB = new ConnectionDB();

    private String wh_no_detail;
    private String part_id, type_id, record_id, image;
    private TextView txt_part, txt_relate, txt_group, txt_detail, txt_area, txt_qty, txt_type;
    private EditText edt_quantity_new;
    private ImageView image_product;
    private ProgressBar progressBar;
    private LinearLayout linear_detail;

    ////data from fetch
    String part, relate, group, qty_show, qty_old, qty_new, detail, area1, area2, area3, area4, type;

    //test
    public static String adapter_position;

    public DetailWh01Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_wh01, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ////for back current category
        wh_no_detail = getArguments().getString("wh_no_detail");
        adapter_position = getArguments().getString("adapter_position");


        txt_part = view.findViewById(R.id.txt_part);
        txt_relate = view.findViewById(R.id.txt_relate);
        txt_group = view.findViewById(R.id.txt_group);
        txt_detail = view.findViewById(R.id.txt_detail);
        txt_area = view.findViewById(R.id.txt_area_all);
        txt_qty = view.findViewById(R.id.txt_qty);
        txt_type = view.findViewById(R.id.txt_type);
        edt_quantity_new = view.findViewById(R.id.edt_real_count);
        image_product = view.findViewById(R.id.image_detail);
        progressBar = view.findViewById(R.id.progress_bar);
        linear_detail = view.findViewById(R.id.linear_detail_product);

        part_id = getArguments().getString("part_id");
        type_id = getArguments().getString("type_id");
        record_id = getArguments().getString("record_id");
        image = getArguments().getString("image");

        ///get data
        new GetDetail().execute();

        //save quantity product
        view.findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new InsertData().execute();
            }
        });
        view.findViewById(R.id.txt_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                ShowListWH01Fragment fragment = new ShowListWH01Fragment();

                bundle.putString("wh_no", wh_no_detail);

                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.detail_contianer, fragment).commit();
            }
        });


        ////set header on GONE
        View view_header = getActivity().findViewById(R.id.linear_header);
        view_header.setVisibility(View.GONE);

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


                    String query = "select v_pro.id,v_pro.part,v_pro.relate,pn.description,ld3.name,pt.type_name,st.st_name,std.detail_name,v_pro.area_name,v_pro.row_name,v_pro.column_no,\n" +
                            "CASE\n" +
                            "   WHEN v_pro.shelf = 0 THEN '-' \n" +
                            "   WHEN v_pro.shelf != 0 THEN v_pro.shelf\n" +
                            "   ELSE '-'" +
                            "END as shelf\n" +
                            ",FORMAT(v_pro.qty,'N0')as qty_show,FORMAT(v_pro.qty,'0')as qty_old,FORMAT(cp.count_new,'0')as count_new from Sys_WarehouseManagement..v_Products_Description v_pro \n" +
                            "    LEFT JOIN [ST_TEST].[dbo].[mob_check_partnumber] cp ON v_pro.part_id = cp.part_id_checked and v_pro.type_id = cp.type_id_check and v_pro.id=cp.record_part\n" +
                            "    INNER JOIN [Sys_DataMaster].[dbo].[sys_partnumber_group] pg ON v_pro.part_id = pg.part_id\n" +
                            "    INNER JOIN [Sys_DataMaster].[dbo].[layer3_data] ld3 ON pg.type_id = ld3.id\n" +
                            "    INNER JOIN [Sys_DataMaster].[dbo].[sys_partnumber_type] spnt ON v_pro.part_id = spnt.part_id\n" +
                            "    INNER JOIN  [Sys_DataMaster].[dbo].[partnumber_type] pt ON spnt.type_part = pt.record_id\n" +
                            "    INNER JOIN [Sys_DataMaster].[dbo].[st_type] st ON spnt.work_type = st.record_id\n" +
                            "    INNER JOIN [Sys_DataMaster].[dbo].[st_type_detail] std ON spnt.detail_type = std.record_id\n" +
                            "    INNER JOIN [Sys_DataMaster].[dbo].[sys_partnumber_new] pn ON pn.record_id = v_pro.part_id\n" +
                            "    where  v_pro.part_id = " + part_id + " and v_pro.id=" + record_id + "";

//                    and v_pro.IsDelete=0 and pg.IsDelete=0 and ld3.IsDelete=0 and spnt.IsDelete=0\n" +
//                    "and pt.IsDelete=0 and st.IsDelete=0 and std.IsDelete=0 and v_pro.IsDelete=0";

                    Log.d("GetDetail", query);
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
                        qty_show = rs.getString("qty_show");
                        qty_old = rs.getString("qty_old");
                        qty_new = rs.getString("count_new");

                    }

                    conn.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "can't connect to server !!, please contact admin.", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressBar.setVisibility(View.GONE);
            linear_detail.setVisibility(View.VISIBLE);

            ////Check which warehouse for add ext.
            if (WareHouseActivity.category_id.equals("31")) {

                if (type_id.equals("1")) {
                    txt_part.setText(part + "JAP");
                } else if (type_id.equals("2")) {
                    txt_part.setText(part + "HIC");
                } else if (type_id.equals("13")) {
                    txt_part.setText(part + "");
                } else if (type_id.equals("15")) {
                    txt_part.setText(part + "STL");
                } else {
                    txt_part.setText(part + "ORI");
                }

            } else if (WareHouseActivity.category_id.equals("32")) {

                if (type_id.equals("1")) {
                    txt_part.setText(part + "");
                } else if (type_id.equals("2")) {
                    txt_part.setText(part + "YSS");
                } else {
                    txt_part.setText(part + "KYB");
                }
            } else {
                txt_part.setText(part + "");
            }

            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/MavenPro-Regular.ttf");
            txt_part.setTypeface(tf, Typeface.BOLD);

            txt_relate.setText("พาร์ทนัมเบอร์ที่เกี่ยวข้อง : " + relate);
            txt_detail.setText("รายละเอียดสินค้า : " + detail);
            txt_group.setText("กลุ่มสินค้า : " + group);
            txt_type.setText("ประเภทสินค้า : " + type);


            String area_sum = area1 != null ? area1 : "-";
            area_sum += " / ";
            area_sum += area2 != null ? area2 : "-";
            area_sum += " / ";
            area_sum += area3 != null ? area3 : "-";
            area_sum += " / ";
            area_sum += area4.equals("0") ? "-" : area4;
            txt_area.setText(area_sum);


            ///get text to label
            if (qty_new != null) {
                txt_qty.setText(qty_new);
                edt_quantity_new.setText(String.valueOf(qty_new));
            } else {
                txt_qty.setText(qty_show);
                edt_quantity_new.setText(String.valueOf(qty_old));
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

//                    String query = "IF not EXISTS(select * from Sys_WarehouseManagement..v_Products_Description v_pro LEFT JOIN ST_TEST..mob_check_partnumber cp\n" +
//                            "    ON v_pro.part_id = cp.part_id_checked and record_part = "+record_id+" WHERE part_id_checked = "+part_id+" and type_id_check = "+type_id+" and v_pro.id="+record_id+")\n" +
//                            "            BEGIN\n" +
//                            "                 insert into ST_TEST..mob_check_partnumber values("+record_id+",'"+part+"',"+part_id+","+type_id+","+qty+","+edt_quantity_new.getText().toString()+",GETDATE(),'"+ MainActivity.name+"');\n" +
//                            "            END\n" +
//                            "            ELSE\n" +
//                            "            BEGIN\n" +
//                            "              update ST_TEST..mob_check_partnumber set count_new = "+edt_quantity_new.getText().toString()+",date_check=GETDATE(),name_check='"+MainActivity.name+"' where part_id_checked = "+part_id+" and type_id_check = "+type_id+" and record_part = "+record_id+"\n" +
//                            "            END";

                    String query = "IF not EXISTS(select * from Sys_WarehouseManagement..v_Products_Description v_pro LEFT JOIN ST_TEST..mob_check_partnumber cp\n" +
                            "    ON v_pro.part_id = cp.part_id_checked and record_part = " + record_id + " WHERE part_id_checked = " + part_id + "  and v_pro.id=" + record_id + ")\n" +
                            "            BEGIN\n" +
                            "                 insert into ST_TEST..mob_check_partnumber values(" + record_id + ",'" + part + "'," + part_id + "," + type_id + "," + qty_old + "," + edt_quantity_new.getText().toString() + ",GETDATE(),'" + MainActivity.name + "');\n" +
                            "            END\n" +
                            "            ELSE\n" +
                            "            BEGIN\n" +
                            "              update ST_TEST..mob_check_partnumber set count_new = " + edt_quantity_new.getText().toString() + ",date_check=GETDATE(),name_check='" + MainActivity.name + "' where part_id_checked = " + part_id + "  and record_part = " + record_id + "\n" +
                            "            END";

                    Log.d("InsertData", query);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {
                    }

                    conn.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "can't connect to server !!, please contact admin.", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            Bundle bundle = new Bundle();
            ShowListWH01Fragment fragment = new ShowListWH01Fragment();

            bundle.putString("wh_no", wh_no_detail);
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.detail_contianer, fragment).commit();

            super.onPostExecute(aVoid);
        }
    }
}
