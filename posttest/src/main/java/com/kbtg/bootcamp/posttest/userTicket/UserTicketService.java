package com.kbtg.bootcamp.posttest.userTicket;

import com.kbtg.bootcamp.posttest.exception.CustomException;
import com.kbtg.bootcamp.posttest.lottery.LotteryEntity;
import com.kbtg.bootcamp.posttest.lottery.LotteryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserTicketService {

    private final UserTicketRepository userTicketRepository;
    private final LotteryRepository lotteryRepository;

    public UserTicketService(UserTicketRepository userTicketRepository, LotteryRepository lotteryRepository) {
        this.userTicketRepository = userTicketRepository;
        this.lotteryRepository = lotteryRepository;
    }

    public BuyTicketResponse buyTicket(String userId, String ticket) {
        try {

            LotteryEntity lotteryEntity = lotteryRepository.findByTicket(ticket);

            if (lotteryEntity == null) {
                log.error("Ticket not found");
                throw new CustomException(HttpStatus.NOT_FOUND, "Ticket not found");
            }

            UserTicketEntity userTicketEntity = new UserTicketEntity();
            userTicketEntity.setUserId(userId);
            userTicketEntity.setLottery(lotteryEntity);

            UserTicketEntity saved = userTicketRepository.save(userTicketEntity);

            return new BuyTicketResponse(saved.getId().toString());

        } catch (CustomException ce) {
            throw new CustomException(ce.getHttpStatus(), ce.getMessage());
        } catch (Exception e) {
            log.error("error while buy ticket", e);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Buy ticket failed");
        }
    }
}
