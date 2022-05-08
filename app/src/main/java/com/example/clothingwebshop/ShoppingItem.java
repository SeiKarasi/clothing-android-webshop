package com.example.clothingwebshop;

public class ShoppingItem {
    private String id;
    private String name;
    private String info;
    private String price;
    private float rated;
    private int imageResource;
    private int add_to_cart_count;

    public ShoppingItem(){}

    public ShoppingItem(String name, String info, String price, float rated, int imageResource, int add_to_cart_count) {
        this.name = name;
        this.info = info;
        this.price = price;
        this.rated = rated;
        this.imageResource = imageResource;
        this.add_to_cart_count = add_to_cart_count;
    }

    public String _getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public String getPrice() {
        return price;
    }

    public float getRated() {
        return rated;
    }

    public int getImageResource() {
        return imageResource;
    }

    public int getAdd_to_cart_count() {
        return add_to_cart_count;
    }

    public void setId(String id) {
        this.id = id;
    }
}
