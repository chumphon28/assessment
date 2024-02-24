package com.kbtg.bootcamp.posttest.service;

import com.kbtg.bootcamp.posttest.model.CustomException;
import com.kbtg.bootcamp.posttest.model.Ticket;
import com.kbtg.bootcamp.posttest.model.lottery.AddLotteryRequest;
import com.kbtg.bootcamp.posttest.model.lottery.LotteryEntity;
import com.kbtg.bootcamp.posttest.repository.LotteryReposity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LotteryService {
    private LotteryReposity lotteryReposity;

    public LotteryService(LotteryReposity lotteryReposity) {
        this.lotteryReposity = lotteryReposity;
    }

    public Ticket addTicket(AddLotteryRequest request) {
        try {
            LotteryEntity lotteryEntity = new LotteryEntity();
            lotteryEntity.setTicket(request.getTicket());
            lotteryEntity.setPrice(request.getPrice());
            lotteryEntity.setAmount(request.getAmount());
            lotteryReposity.save(lotteryEntity);
            return new Ticket(lotteryEntity.getTicket());
        } catch (Exception e) {
            log.error("error add ticket: {}, error: {}", request.getTicket(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Add ticket failed");
        }
    }
}
