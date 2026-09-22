package com.example.smartpantrymanager.model;

/** One ingredient the user currently has at home. */
public class PantryItem {
    private final long id;  //-1 when not yet saved to the database
    private final String name;;
    private final double quantity;
    private final String unit;
    private final String expiryDate;    //"yyyy-MM-dd" or null if none given

    public PantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public long getId() {return id;}
    public String getName() {return name;}
    public double getQuantity() {return quantity;}
    public String getUnit() {return unit;}
    public String getExpiryDate() {return expiryDate;}

}
