package com.zebra.rfid.demo.pslsdksample.modals;

/**
 * Created by Admin on 18/Dec/2018.
 */

public class SkuModal {

    String sku;
    String epc;
    String name;
    String size;
    String color;
    String image_url;
    int quantity=0;
    String is_found;
    String shop,zone;
    String asn;
    String stock;

    String from,to;

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

    String brand_name,style_name;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getAsn() {
        return asn;
    }

    public void setAsn(String asn) {
        this.asn = asn;
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

    public String getIs_found() {
        return is_found;
    }

    public void setIs_found(String is_found) {
        this.is_found = is_found;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
