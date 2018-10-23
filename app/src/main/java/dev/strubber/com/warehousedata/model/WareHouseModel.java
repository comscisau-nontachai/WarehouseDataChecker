package dev.strubber.com.warehousedata.model;

public class WareHouseModel {

    public  String warehouse_name,warehouse_no;
    public String part,relate,quantity,area1,area2,area3,area4,part_id,type_id;
    public  String count_new,image,date_check,record_id,warehouse_no_search;


    public WareHouseModel(String warehouse_name, String warehouse_no) {
        this.warehouse_name = warehouse_name;
        this.warehouse_no = warehouse_no;
    }

    public WareHouseModel(String part, String relate, String quantity, String area1, String area2, String area3,
                          String area4,String part_id,String type_id,String count_new,String image,String date_check,String record_id
                            ,String warehouse_no_search) {
        this.part = part;
        this.relate = relate;
        this.quantity = quantity;
        this.area1 = area1;
        this.area2 = area2;
        this.area3 = area3;
        this.area4 = area4;
        this.part_id = part_id;
        this.type_id = type_id;
        this.count_new = count_new;
        this.image = image;
        this.date_check = date_check;
        this.record_id = record_id;
        this.warehouse_no_search = warehouse_no_search;
    }


}
