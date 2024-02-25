package com.kbtg.bootcamp.posttest.userTicket;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetUserTicketResponse {
    private List<String> tickets;
    private int count;
    private int cost;
}
