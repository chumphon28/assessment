package com.kbtg.bootcamp.posttest.lottery;

import com.kbtg.bootcamp.posttest.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class LotteryService {
    private final LotteryRepository lotteryRepository;

    public LotteryService(LotteryRepository lotteryRepository) {
        this.lotteryRepository = lotteryRepository;
    }

    public AddLotteryResponse addTicket(AddLotteryRequest request) {
        try {
            LotteryEntity lotteryEntity = new LotteryEntity();
            lotteryEntity.setTicket(request.getTicket());
            lotteryEntity.setPrice(request.getPrice());
            lotteryEntity.setAmount(request.getAmount());
            lotteryRepository.save(lotteryEntity);
            return new AddLotteryResponse(lotteryEntity.getTicket());
        } catch (Exception e) {
            log.error("error add ticket: {}, error: {}", request.getTicket(), e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Add ticket failed");
        }
    }


    public GetAllTicketResponse getAllTicket() {
        try {
            List<String> listTicket = lotteryRepository.findTickets();
            log.info("ticket count: {}", listTicket.size());

            return new GetAllTicketResponse(listTicket);
        } catch (Exception e) {
            log.error("error while get all ticket", e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Get All ticket failed");
        }
    }
}
