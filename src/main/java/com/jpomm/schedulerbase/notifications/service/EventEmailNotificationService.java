package com.jpomm.schedulerbase.notifications.service;

import com.jpomm.schedulerbase.notifications.dto.EventEmailJobResult;
import com.jpomm.schedulerbase.notifications.dto.PendingEventEmailNotification;

import java.util.List;

public interface EventEmailNotificationService {

    List<PendingEventEmailNotification> listPendingNotifications();

    EventEmailJobResult processPendingNotifications();
}
