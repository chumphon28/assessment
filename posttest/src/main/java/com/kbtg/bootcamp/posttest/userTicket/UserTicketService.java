package com.kbtg.bootcamp.posttest.userTicket;

import com.kbtg.bootcamp.posttest.exception.CustomException;
import com.kbtg.bootcamp.posttest.lottery.LotteryEntity;
import com.kbtg.bootcamp.posttest.lottery.LotteryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserTicketService {

    private final UserTicketRepository userTicketRepository;
    private final LotteryRepository lotteryRepository;

    public UserTicketService(UserTicketRepository userTicketRepository, LotteryRepository lotteryRepository) {
        this.userTicketRepository = userTicketRepository;
        this.lotteryRepository = lotteryRepository;
    }

    @Transactional
    public BuyTicketResponse buyTicket(String userId, String ticket) {
        try {

            LotteryEntity lotteryEntity = lotteryRepository.findByTicket(ticket);

            if (ObjectUtils.isEmpty(lotteryEntity)) {
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
        } catch (Exception ex) {
            log.error("error while buy ticket", ex);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Buy ticket failed");
        }
    }

    public GetUserTicketResponse getUserTicket(String userId) {

        try {
            List<UserTicketEntity> userTickets = userTicketRepository.findByUserId(userId);
            if (ObjectUtils.isEmpty(userTickets)) {
                log.error("Ticket Not found on userId: {}", userId);
                throw new CustomException(HttpStatus.NOT_FOUND, "Ticket Not found on userId: " + userId);
            }

            List<String> tickets = new ArrayList<>();
            int count = 0;
            int cost = 0;

            for (UserTicketEntity userTK : userTickets) {
                tickets.add(userTK.getLottery().getTicket());
                count += 1;
                cost += (userTK.getLottery().getPrice());
            }

            return new GetUserTicketResponse(tickets, count, cost);

        } catch (CustomException ce) {
            throw new CustomException(ce.getHttpStatus(), ce.getMessage());
        } catch (Exception ex) {
            log.error("error while buy ticket", ex);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Get user ticket failed");
        }
    }

    @Transactional
    public DeleteUserTicketResponse deleteUserTicket(String userId, String ticket) {

        try {
            int affectRow = userTicketRepository.deleteByUserIdAndTicket(userId, ticket);

            if (affectRow < 1) {
                log.warn("delete user ticket 0 record");
                throw new CustomException(HttpStatus.NOT_FOUND, "ticket or userId not found");
            }

            return new DeleteUserTicketResponse(ticket);
        } catch (CustomException ce) {
            throw new CustomException(ce.getHttpStatus(), ce.getMessage());
        } catch (Exception ex) {
            log.error("error while delete ticket with userId: {}", userId, ex);
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "delete user ticket failed");
        }
    }
}
