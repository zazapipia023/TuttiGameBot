package com.zaza.tuttifruttibot.models;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "player")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "action")
    private String action;

    @Column(name = "name")
    private String name;

    @Column(name = "cream_value")
    private Integer value;

    @Column(name = "profit")
    private Integer profit;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IceShop> shops = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id) && Objects.equals(action, player.action) && Objects.equals(name, player.name) && Objects.equals(value, player.value) && Objects.equals(profit, player.profit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, action, name, value, profit);
    }
}
