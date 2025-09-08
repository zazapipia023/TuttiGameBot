package com.zaza.tuttifruttibot.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "player")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "cream_value")
    private Integer value;

    @Column(name = "profit")
    private Integer profit;

}
