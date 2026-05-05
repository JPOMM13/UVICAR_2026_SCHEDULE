package com.jpomm.schedulerbase.notifications.dto;

public record EventEmailJobResult(
        int fetchedCount,
        int sentCount
) {
}
