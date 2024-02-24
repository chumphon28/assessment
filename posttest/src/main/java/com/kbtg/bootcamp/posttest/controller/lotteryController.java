package com.kbtg.bootcamp.posttest.controller;

import com.kbtg.bootcamp.posttest.model.CustomException;
import com.kbtg.bootcamp.posttest.model.Ticket;
import com.kbtg.bootcamp.posttest.model.lottery.AddLotteryRequest;
import com.kbtg.bootcamp.posttest.model.lottery.AddLotteryResponse;
import com.kbtg.bootcamp.posttest.service.LotteryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class lotteryController {

    private LotteryService lotteryService;

    public lotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @PostMapping("/admin/lotteries")
    public ResponseEntity<AddLotteryResponse> addTicket(
            @Validated
            @RequestBody AddLotteryRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Require some field");
        }

        Ticket addedTicket = lotteryService.addTicket(request);

        return new ResponseEntity<>(new AddLotteryResponse(addedTicket),HttpStatus.OK);
    }
}
