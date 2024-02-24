package com.kbtg.bootcamp.posttest.repository;

import com.kbtg.bootcamp.posttest.model.lottery.LotteryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotteryReposity extends JpaRepository<LotteryEntity, Integer> {

}
