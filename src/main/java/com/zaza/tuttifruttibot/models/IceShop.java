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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ice_shop_seq")
    @SequenceGenerator(
            name = "ice_shop_seq",
            sequenceName = "ice_shop_id_seq",
            allocationSize = 50
    )
    @Column(name = "id")
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "ice_shop_upgrades",
            joinColumns = @JoinColumn(name = "ice_shop_id")
    )
    @Column(name = "upgrades")
    private List<String> upgrades;

    @Column(name = "total_profit")
    private Integer totalProfit;

    @Column(name = "total_cream")
    private Integer totalCream;

}
