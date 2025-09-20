package com.zaza.tuttifruttibot.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ice_shop")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IceShop {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_name")
    private String shopName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @Column(name = "cream_value")
    private Integer value;

    @Column(name = "profit")
    private Integer profit;

    @ElementCollection
    @Column(name = "upgrades")
    private List<String> upgrades;

}
