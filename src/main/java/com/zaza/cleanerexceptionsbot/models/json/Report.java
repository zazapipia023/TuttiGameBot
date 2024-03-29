package com.zaza.cleanerexceptionsbot.models.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Report {

    private String club_id;

    private int totals;

    private int free_b;

    private int free_a;

    private String pc;

    private String ip;

}
