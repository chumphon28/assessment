package com.kbtg.bootcamp.posttest.userTicket;

import com.kbtg.bootcamp.posttest.lottery.LotteryEntity;
import com.kbtg.bootcamp.posttest.lottery.LotteryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.zip.DataFormatException;

import static com.kbtg.bootcamp.posttest.Utils.assertException;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserTicketControllerTest {

    MockMvc mockMvc;
    HttpHeaders httpHeaders;

    @Mock
    UserTicketService userTicketService;
    @Mock
    UserTicketRepository userTicketRepository;
    @Mock
    LotteryRepository lotteryRepository;

    private final String BUY_TICKET_URL = "/users/%s/lotteries/%s";

    @BeforeEach
    void setup() {

        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        userTicketService = new UserTicketService(userTicketRepository, lotteryRepository);
        UserTicketController userTicketController = new UserTicketController(userTicketService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userTicketController)
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("should return id, when perform POST: /users/:userId/lotteries/:ticketId")
    void buyTicketSuccess() throws Exception {
        String url = String.format(BUY_TICKET_URL, "0000011111", "123456");

        LotteryEntity lottery = new LotteryEntity();
        lottery.setId(1);

        UserTicketEntity userTicketEntity = new UserTicketEntity();
        userTicketEntity.setId(999);
        userTicketEntity.setLottery(lottery);

        when(userTicketRepository.save(any(UserTicketEntity.class))).thenReturn(userTicketEntity);
        when(lotteryRepository.findByTicket(anyString())).thenReturn(lottery);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .headers(httpHeaders))
                .andExpect(jsonPath("$.id", is(String.valueOf(userTicketEntity.getId()))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return 400, when perform POST: /users/:userId/lotteries/:ticketId, with userId length < 10")
    void buyTicketFailedUserIdLengthLess() {
        String url = String.format(BUY_TICKET_URL, "000", "123456");

        try {
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .headers(httpHeaders));
            fail("Should throw exception");
        } catch (Exception ex) {
            assertException(HttpStatus.BAD_REQUEST, ex);
        }

    }

    @Test
    @DisplayName("should return 400, when perform POST: /users/:userId/lotteries/:ticketId, with userId length > 10")
    void buyTicketFailedUserIdLengthMore() {
        String url = String.format(BUY_TICKET_URL, "00000111112", "123456");

        try {
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .headers(httpHeaders));
            fail("Should throw exception");
        } catch (Exception ex) {
            assertException(HttpStatus.BAD_REQUEST, ex);
        }

    }

    @Test
    @DisplayName("should return 400, when perform POST: /users/:userId/lotteries/:ticketId, with ticketId length < 6")
    void buyTicketFailedTicketLengthLess() {
        String url = String.format(BUY_TICKET_URL, "0000011111", "123");

        try {
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .headers(httpHeaders));
            fail("Should throw exception");
        } catch (Exception ex) {
            assertException(HttpStatus.BAD_REQUEST, ex);
        }
    }

    @Test
    @DisplayName("should return 400, when perform POST: /users/:userId/lotteries/:ticketId, with ticketId length > 6")
    void buyTicketFailedTicketLengthMore() {
        String url = String.format(BUY_TICKET_URL, "0000011111", "1234567");

        try {
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .headers(httpHeaders));
            fail("Should throw exception");
        } catch (Exception ex) {
            assertException(HttpStatus.BAD_REQUEST, ex);
        }
    }

    @Test
    @DisplayName("should return 500, when perform POST: /users/:userId/lotteries/:ticketId, error while retrieve ticket from DB")
    void buyTicketFailedGetTicketFromDB() {
        String url = String.format(BUY_TICKET_URL, "0000011111", "123456");

        doThrow(new DataAccessException("Mocked data access exception") {
        }).when(lotteryRepository).findByTicket(anyString());

        try {
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .headers(httpHeaders));
        } catch (Exception ex) {
            assertException(HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }

    }

    @Test
    @DisplayName("should return 404, when perform POST: /users/:userId/lotteries/:ticketId, retrieve with ticket not found")
    void buyTicketFailedTicketNotFound() {
        String url = String.format(BUY_TICKET_URL, "0000011111", "123456");

        when(lotteryRepository.findByTicket(anyString())).thenReturn(null);

        try {
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .headers(httpHeaders));
        } catch (Exception ex) {
            assertException(HttpStatus.NOT_FOUND, ex);
        }

    }

}