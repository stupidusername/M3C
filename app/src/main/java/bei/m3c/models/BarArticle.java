package bei.m3c.models;

import java.math.BigDecimal;

public class BarArticle {

    public int id;
    public String name;
    public String description;
    public BigDecimal price;
    public String pictureUrl;

    public BarArticle(int id, String name, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
