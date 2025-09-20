package com.zaza.tuttifruttibot.models;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "player")
@Data
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

}
