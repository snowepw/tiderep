package net.xdproston.tiderep;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;

@Data
@DatabaseTable(tableName = "users")
public class User {
    @DatabaseField(id = true)
    private String name;

    @DatabaseField
    private int reputation;

    @DatabaseField
    private String sends;

    public User() {
        // ORMLite requires no-arg constructor
    }

    public User(String name, int reputation, String sends) {
        this.name = name;
        this.reputation = reputation;
        this.sends = sends;
    }
}
