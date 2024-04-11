package org.example;

import java.util.List;

public class Pizza {
    private String name;
    private List<String> ingredients;
    private double price;

    public Pizza(String name, List<String> ingredients, double price) {
        this.name = name;
        this.ingredients = ingredients;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public double getPrice() {
        return price;
    }
}
