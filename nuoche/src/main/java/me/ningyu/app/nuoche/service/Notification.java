package me.ningyu.app.nuoche.service;

import me.ningyu.app.nuoche.common.dto.MessageDTO;

public interface Notification
{
    void send(MessageDTO message);
}
