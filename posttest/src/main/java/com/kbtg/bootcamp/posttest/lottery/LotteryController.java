package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LotteryController {

    private final LotteryService lotteryService;

    public LotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @PostMapping(value = "/admin/lotteries", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AddLotteryResponse> addTicket(
            @Validated
            @RequestBody AddLotteryRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Invalid input");
        }

        AddLotteryResponse addedAddLotteryResponse = lotteryService.addTicket(request);

        return new ResponseEntity<>(addedAddLotteryResponse, HttpStatus.CREATED);
    }

    @GetMapping("/lotteries")
    public ResponseEntity<GetAllTicketResponse> getAllTicket(){
        GetAllTicketResponse getAllTicketResponse = lotteryService.getAllTicket();

        return new ResponseEntity<>(getAllTicketResponse, HttpStatus.OK);
    }

}
