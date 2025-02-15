package com.kbtg.bootcamp.posttest.userTicket;

import com.kbtg.bootcamp.posttest.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserTicketController {

    private final UserTicketService userTicketService;

    public UserTicketController(UserTicketService userTicketService) {
        this.userTicketService = userTicketService;
    }

    @PostMapping(value = "/users/{userId}/lotteries/{ticketId}")
    public ResponseEntity<BuyTicketResponse> buyTicket(
            @PathVariable("userId")
            String userId,
            @PathVariable("ticketId")
            String ticketId) {

        if (validateBuyTicketBody(userId, 10) || validateBuyTicketBody(ticketId, 6)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid input");
        }

        BuyTicketResponse buyTicketResponse = userTicketService.buyTicket(userId, ticketId);

        return new ResponseEntity<>(buyTicketResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/users/{userId}/lotteries")
    public ResponseEntity<GetUserTicketResponse> getUserTicket(@PathVariable("userId") String userId) {
        if (validateBuyTicketBody(userId, 10)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid input");
        }

        GetUserTicketResponse getUserTicketResponse = userTicketService.getUserTicket(userId);

        return new ResponseEntity<>(getUserTicketResponse, HttpStatus.OK);
    }

    @DeleteMapping(value = "/users/{userId}/lotteries/{ticketId}")
    public ResponseEntity<DeleteUserTicketResponse> getUserTicket(
            @PathVariable("userId")
            String userId,
            @PathVariable("ticketId")
            String ticketId) {
        if (validateBuyTicketBody(userId, 10) || validateBuyTicketBody(ticketId, 6)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid input");
        }

        DeleteUserTicketResponse deleteUserTicketResponse = userTicketService.deleteUserTicket(userId, ticketId);

        return new ResponseEntity<>(deleteUserTicketResponse, HttpStatus.OK);
    }

    private boolean validateBuyTicketBody(String input, int size) {
        return input == null || input.isEmpty() || input.length() != size;
    }
}
