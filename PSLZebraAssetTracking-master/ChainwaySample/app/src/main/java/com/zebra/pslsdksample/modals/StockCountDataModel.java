package com.zebra.pslsdksample.modals;

import android.databinding.BaseObservable;

public class StockCountDataModel extends BaseObservable {
    public int per;

    public String inventory_type;
    public String is_updated;
    public String item_name,size,item_color,is_found,img_url,stock;
    public String shop,zone,source,destination;
    public String distinct_barcodes_count;

    public boolean is_image_cached = false;

    public String brand_name,style_name;

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getStyle_name() {
        return style_name;
    }

    public void setStyle_name(String style_name) {
        this.style_name = style_name;
    }

    public String sync_date_time;

    public boolean isIs_image_cached() {
        return is_image_cached;
    }

    public void setIs_image_cached(boolean is_image_cached) {
        this.is_image_cached = is_image_cached;
    }

    public String getSync_date_time() {
        return sync_date_time;
    }

    public void setSync_date_time(String sync_date_time) {
        this.sync_date_time = sync_date_time;
    }

    public String getDistinct_barcodes_count() {
        return distinct_barcodes_count;
    }

    public void setDistinct_barcodes_count(String distinct_barcodes_count) {
        this.distinct_barcodes_count = distinct_barcodes_count;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getInventory_type() {
        return inventory_type;
    }

    public void setInventory_type(String inventory_type) {
        this.inventory_type = inventory_type;
    }

    public String getIs_updated() {
        return is_updated;
    }

    public void setIs_updated(String is_updated) {
        this.is_updated = is_updated;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getItem_color() {
        return item_color;
    }

    public void setItem_color(String item_color) {
        this.item_color = item_color;
    }

    public String getIs_found() {
        return is_found;
    }

    public void setIs_found(String is_found) {
        this.is_found = is_found;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public int getPer() {
        return per;
    }

    public void setPer(int per) {
        this.per = per;
    }

    public String count = "0";
    public String epc,barcode,tid;
    public String distinct_count;

    public String getDistinct_count() {
        return distinct_count;
    }

    public void setDistinct_count(String distinct_count) {
        this.distinct_count = distinct_count;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

   /* @BindingAdapter("profileImage")
    public static void loadImage(ImageView view, String imageUrl) {
        Glide.with(view.getContext())
                .load(imageUrl).apply(new RequestOptions().placeholder(R.drawable.no_image))
                .into(view);
    }*/

}