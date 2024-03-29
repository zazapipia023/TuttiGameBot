package com.zaza.cleanerexceptionsbot.controllers;

import com.zaza.cleanerexceptionsbot.services.ClubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/exceptions")
public class ExceptionsController {

    private final ClubService clubService;

    @GetMapping("/steam")
    public List<String> getSteamExceptions(@RequestParam String clubId) {
        return clubService.findOne(clubId).getSteamGames().keySet().stream().toList();
    }

    @GetMapping("/manifest")
    public List<String> getManifestExceptions(@RequestParam String clubId) {
        return clubService.findOne(clubId).getSteamGames().values().stream().toList();
    }

    @GetMapping("/egs")
    public List<String> getEgsExceptions(@RequestParam String clubId) {
        return clubService.findOne(clubId).getEgsGames();
    }

    @GetMapping("/vk")
    public List<String> getVkExceptions(@RequestParam String clubId) {
        return clubService.findOne(clubId).getVkGames();
    }

    @GetMapping("/ubisoft")
    public List<String> getUbisoftExceptions(@RequestParam String clubId) {
        return clubService.findOne(clubId).getUbisoftGames();
    }

    @GetMapping("/battlenet")
    public List<String> getBattleNetExceptions(@RequestParam String clubId) {
        return clubService.findOne(clubId).getBattleNetGames();
    }
}
