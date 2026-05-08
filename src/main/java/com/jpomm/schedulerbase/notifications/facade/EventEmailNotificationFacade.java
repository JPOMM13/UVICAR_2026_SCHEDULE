package com.jpomm.schedulerbase.notifications.facade;

import com.jpomm.schedulerbase.notifications.dto.EventEmailJobResult;
import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;

import java.util.List;

public interface EventEmailNotificationFacade {

    List<PendingEventEmailNotification> listPendingNotifications(int razTra);

    EventEmailJobResult processPendingNotifications(int razTra);
}
