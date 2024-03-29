package com.zaza.cleanerexceptionsbot.controllers;

import com.zaza.cleanerexceptionsbot.models.postgres.TelegramClient;
import com.zaza.cleanerexceptionsbot.sender.TelegramSender;
import com.zaza.cleanerexceptionsbot.services.TelegramClientService;
import com.zaza.cleanerexceptionsbot.models.json.Report;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReportController {

    private final TelegramSender telegramSender;
    private final TelegramClientService clientService;

    @PostMapping("/report")
    public ResponseEntity<HttpStatus> sendReport(@RequestBody Report report,
                                                 BindingResult bindingResult) {

        log.info("Sending report after clean process, club id: " + report.getClub_id());
        TelegramClient client = clientService.findByClubId(report.getClub_id());

        telegramSender.sendMessage(client.getId(), buildReport(report));

        return ResponseEntity.ok(HttpStatus.OK);
    }

    private String buildReport(Report report) {
        StringBuilder msg = new StringBuilder().append("Отчет об очистке ПК\n\n")
                .append("PC: ").append(report.getPc()).append("\n")
                .append("Объем диска: ").append(report.getTotals()).append(" GB\n")
                .append("Было свободно до очистки: ").append(report.getFree_b()).append(" GB\n")
                .append("Стало свободно после очистки: ").append(report.getFree_a()).append(" GB");

        return msg.toString();
    }
}
