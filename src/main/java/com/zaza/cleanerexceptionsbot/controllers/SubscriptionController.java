package com.zaza.cleanerexceptionsbot.controllers;

import com.zaza.cleanerexceptionsbot.services.ClubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/subscription")
public class SubscriptionController {

    private final ClubService clubService;


    @GetMapping("")
    public Boolean isSubExpired(@RequestParam String clubId) {
        LocalDateTime expirationDate = clubService.findOne(clubId).getSubDate().plus(30, ChronoUnit.DAYS);
        LocalDateTime currentDate = LocalDateTime.now();

        return currentDate.isAfter(expirationDate);
    }
}
