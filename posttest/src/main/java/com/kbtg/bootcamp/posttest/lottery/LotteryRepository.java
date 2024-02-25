package com.kbtg.bootcamp.posttest.lottery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LotteryRepository extends JpaRepository<LotteryEntity, Integer> {

    @Query("SELECT l.ticket FROM LotteryEntity l")
    List<String> findTickets();

    @Query("SELECT l FROM LotteryEntity l WHERE l.ticket = ?1")
    LotteryEntity findByTicket(String ticket);
}
