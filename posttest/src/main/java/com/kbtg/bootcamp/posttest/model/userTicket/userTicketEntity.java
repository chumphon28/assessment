package com.kbtg.bootcamp.posttest.model.userTicket;

import com.kbtg.bootcamp.posttest.model.lottery.LotteryEntity;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "user_ticket")
public class userTicketEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "lottery_id", referencedColumnName = "id", nullable = false)
    private LotteryEntity lottery;
}
