package dev.vkazulkin.entity;

import java.time.LocalDate;

public record UpcomingTalk(String talkName, LocalDate date, String eventURL) {
}


