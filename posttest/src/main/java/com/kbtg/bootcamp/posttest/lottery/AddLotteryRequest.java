package com.kbtg.bootcamp.posttest.lottery;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddLotteryRequest {

    @NotNull
    @Size(min = 6, max = 6)
    private String ticket;

    @NotNull
    private Integer price;

    @NotNull
    private Integer amount;
}
