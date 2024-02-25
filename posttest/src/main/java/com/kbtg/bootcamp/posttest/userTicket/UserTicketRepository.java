package com.kbtg.bootcamp.posttest.userTicket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserTicketRepository extends JpaRepository<UserTicketEntity, Integer> {

    @Query("SELECT u FROM UserTicketEntity u WHERE u.userId = ?1")
    List<UserTicketEntity> findByUserId(String userId);

    @Modifying
    @Query("DELETE FROM UserTicketEntity ut WHERE ut.userId = ?1 AND ut.lottery.ticket = ?2")
    int deleteByUserIdAndTicket(String userId, String ticket);
}
