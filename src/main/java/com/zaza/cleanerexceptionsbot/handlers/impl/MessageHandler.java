package com.zaza.cleanerexceptionsbot.handlers.impl;

import com.zaza.cleanerexceptionsbot.commands.Command;
import com.zaza.cleanerexceptionsbot.commands.Commands;
import com.zaza.cleanerexceptionsbot.commands.impl.*;
import com.zaza.cleanerexceptionsbot.handlers.Handler;
import com.zaza.cleanerexceptionsbot.sender.TelegramSender;
import com.zaza.cleanerexceptionsbot.services.TelegramClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageHandler implements Handler {

    private final TelegramSender telegramSender;
    private final TelegramClientService clientService;
    private final StartCommand startCommand;
    private final RegisterCommand registerCommand;
    private final GetGamesCommand getGamesCommand;
    private final AddSteamGameCommand addSteamGameCommand;
    private final AddEgsGameCommand addEgsGameCommand;
    private final DeleteSteamGameCommand delSteamGameCommand;
    private final DeleteEgsGameCommand delEgsGameCommand;
    private final AddVkGameCommand addVkGameCommand;
    private final DeleteVkGameCommand deleteVkGameCommand;
    private final AddUbisoftGameCommand addUbisoftGameCommand;
    private final DeleteUbisoftGameCommand deleteUbisoftGameCommand;
    private final AddBattleNetGameCommand addBattleNetGameCommand;
    private final DeleteBattleNetGameCommand deleteBattleNetGameCommand;
    private final PayCommand payCommand;
    private final SuccessPayCommand successPayCommand;

    private Map<String, Command<Long>> commands;

    private void createCommandHandlers() {
        commands = new HashMap<>();

        commands.put(Commands.GET_GAMES, getGamesCommand);
        commands.put(Commands.ADD_STEAM_GAME, addSteamGameCommand);
        commands.put(Commands.ADD_EGS_GAME, addEgsGameCommand);
        commands.put(Commands.DELETE_STEAM_GAME, delSteamGameCommand);
        commands.put(Commands.DELETE_EGS_GAME, delEgsGameCommand);
        commands.put(Commands.ADD_VK_GAME, addVkGameCommand);
        commands.put(Commands.DELETE_VK_GAME, deleteVkGameCommand);
        commands.put(Commands.ADD_UBISOFT_GAME, addUbisoftGameCommand);
        commands.put(Commands.DELETE_UBISOFT_GAME, deleteUbisoftGameCommand);
        commands.put(Commands.ADD_BATTLENET_GAME, addBattleNetGameCommand);
        commands.put(Commands.DELETE_BATTLENET_GAME, deleteBattleNetGameCommand);
        commands.put(Commands.PAY_COMMAND, payCommand);
        commands.put(Commands.SUCCESS_PAY_COMMAND, successPayCommand);
        log.info("Set commands");
    }

    @PostConstruct
    public void initialize() {
        createCommandHandlers();
    }

    @Override
    public boolean supports(Update update) {
        if (update.getMessage().hasSuccessfulPayment()) {
            return true;
        }
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }

        String text = update.getMessage().getText();
        return text.startsWith(Commands.START_COMMAND) ||
        text.startsWith(Commands.REGISTER)|| commands.containsKey(text);
    }

    @Override
    public void handle(Update update) {
        Message message = update.getMessage();
        Long chatId = message.getChatId();

        if (message.hasSuccessfulPayment()) {
            processCustomCommand(chatId, "success_pay");
        }

        String text = message.getText();

        log.info("Handling command from: " + chatId);
        if (text.startsWith(Commands.START_COMMAND)) {
            processStartCommand(chatId);
        } else if (text.startsWith(Commands.REGISTER)) {
            processRegisterCommand(chatId);
        } else if (commands.containsKey(text)) {
            processCustomCommand(chatId, text);
        }
        log.info("Handled command from: " + chatId);
    }

    private void processStartCommand(Long chatId) {
        startCommand.execute(chatId);
    }

    private void processRegisterCommand(Long chatId) {
        if (clientService.findOne(chatId).getClubId() == null) {
            registerCommand.execute(chatId);
        } else {
            log.info("User " + chatId + " is not registered");
            telegramSender.sendMessage(chatId, "К данному аккаунту уже зарегистрирован клуб");
        }
    }

    private void processCustomCommand(Long chatId, String command) {
        if (clientService.findOne(chatId).getClubId() != null) {
            commands.get(command).execute(chatId);
        } else {
            log.info("User " + chatId + " is not registered");
            telegramSender.sendMessage(chatId, "К данному аккаунту не зарегистрирован клуб");
        }
    }
}
