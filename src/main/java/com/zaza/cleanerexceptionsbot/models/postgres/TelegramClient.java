package com.zaza.cleanerexceptionsbot.models.postgres;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "client")
@Getter
@Setter
public class TelegramClient {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "club_id")
    private String clubId;

    @Column(name = "action")
    private String action;

}
