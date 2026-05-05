package com.jpomm.schedulerbase.notifications.service;

import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;

public interface EventEmailSender {

    void send(PendingEventEmailNotification notification);
}
