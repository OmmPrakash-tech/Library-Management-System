package com.example.payload.response;

//import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookStatsResponse {

    private long totalActive;
    private long totalAvailable;
    private long totalIssued;
    private long totalOverdue;

    // constructor
    public BookStatsResponse(long totalActive, long totalAvailable,
                             long totalIssued, long totalOverdue) {
        this.totalActive = totalActive;
        this.totalAvailable = totalAvailable;
        this.totalIssued = totalIssued;
        this.totalOverdue = totalOverdue;
    }
}