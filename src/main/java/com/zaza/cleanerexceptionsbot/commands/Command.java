package com.zaza.cleanerexceptionsbot.commands;

public interface Command<T> {

    void execute(T t);

}
