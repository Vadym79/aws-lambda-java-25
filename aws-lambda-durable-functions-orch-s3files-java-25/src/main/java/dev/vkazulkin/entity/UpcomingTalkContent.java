package dev.vkazulkin.entity;

import java.time.LocalDate;

public record UpcomingTalkContent(String talkName, LocalDate date, String eventURL) {
}


