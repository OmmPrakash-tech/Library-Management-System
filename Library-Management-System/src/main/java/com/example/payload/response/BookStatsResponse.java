package com.example.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookStatsResponse {

    private long totalActive;
    private long totalAvailable;
}