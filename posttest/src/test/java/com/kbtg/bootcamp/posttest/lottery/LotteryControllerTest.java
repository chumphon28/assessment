package com.kbtg.bootcamp.posttest.lottery;

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

import java.util.Arrays;
import java.util.List;

import static com.kbtg.bootcamp.posttest.Utils.assertException;
import static com.kbtg.bootcamp.posttest.Utils.stringify;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class LotteryControllerTest {

    MockMvc mockMvc;
    HttpHeaders httpHeaders;
    AddLotteryRequest request;

    @Mock
    LotteryService lotteryService;
    @Mock
    LotteryRepository lotteryRepository;

    private final String ADD_TICKET_URL = "/admin/lotteries";
    private final String GET_ALL_TICKET_URL = "/lotteries";

    @BeforeEach
    void setup() {
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        request = new AddLotteryRequest("111111", 80, 2);

        lotteryService = new LotteryService(lotteryRepository);
        LotteryController lotteryController = new LotteryController(lotteryService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(lotteryController)
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("should return ticket, when perform POST: /admin/lotteries")
    void addTicketSuccess() throws Exception {
        LotteryEntity lottery = new LotteryEntity();
        lottery.setTicket(request.getTicket());
        when(lotteryRepository.save(any(LotteryEntity.class))).thenReturn(lottery);

        mockMvc.perform(MockMvcRequestBuilders.post(ADD_TICKET_URL)
                        .content(stringify(request))
                        .headers(httpHeaders))
                .andExpect(jsonPath("$.ticket", is(request.getTicket())))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("should return 400, when perform POST: /admin/lotteries with ticket length < 6")
    void addTicketFailedTicketLessLength() {
        request.setTicket("12345");
        try {
            mockMvc.perform(MockMvcRequestBuilders.post(ADD_TICKET_URL)
                    .content(stringify(request))
                    .headers(httpHeaders));
            fail("Should throw exception");
        } catch (Exception ex) {
            assertException(HttpStatus.BAD_REQUEST, ex);
        }
    }

    @Test
    @DisplayName("should return 400, when perform POST: /admin/lotteries with ticket length > 6")
    void addTicketFailedTicketOverLength() {
        request.setTicket("1234567");
        try {
            mockMvc.perform(MockMvcRequestBuilders.post(ADD_TICKET_URL)
                    .content(stringify(request))
                    .headers(httpHeaders));
            fail("Should throw exception");
        } catch (Exception ex) {
            assertException(HttpStatus.BAD_REQUEST, ex);
        }
    }

    @Test
    @DisplayName("should return 400, when perform POST: /admin/lotteries with ticket is null")
    void addTicketFailedTicketIsNull() {
        request.setTicket(null);
        try {
            mockMvc.perform(MockMvcRequestBuilders.post(ADD_TICKET_URL)
                    .content(stringify(request))
                    .headers(httpHeaders));
            fail("Should throw exception");
        } catch (Exception ex) {
            assertException(HttpStatus.BAD_REQUEST, ex);
        }
    }

    @Test
    @DisplayName("should return 400, when perform POST: /admin/lotteries with price is null")
    void addTicketFailedPriceIsNull() {
        request.setPrice(null);
        try {
            mockMvc.perform(MockMvcRequestBuilders.post(ADD_TICKET_URL)
                    .content(stringify(request))
                    .headers(httpHeaders));
            fail("Should throw exception");
        } catch (Exception ex) {
            assertException(HttpStatus.BAD_REQUEST, ex);
        }
    }

    @Test
    @DisplayName("should return 400, when perform POST: /admin/lotteries with amount is null")
    void addTicketFailedAmountsNull() {
        request.setPrice(null);
        try {
            mockMvc.perform(MockMvcRequestBuilders.post(ADD_TICKET_URL)
                    .content(stringify(request))
                    .headers(httpHeaders));
            fail("Should throw exception");
        } catch (Exception ex) {
            assertException(HttpStatus.BAD_REQUEST, ex);
        }
    }

    @Test
    @DisplayName("should return 500, when perform POST: /admin/lotteries with insert to DB failed")
    void addTicketFailedWhenInsertDB() {
        try {
            doThrow(new DataAccessException("Mocked data access exception") {
            }).when(lotteryRepository).save(any(LotteryEntity.class));
            mockMvc.perform(MockMvcRequestBuilders.post(ADD_TICKET_URL)
                    .content(stringify(request))
                    .headers(httpHeaders));
            fail("Should throw exception");
        } catch (Exception ex) {
            assertException(HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }
    }

    @Test
    @DisplayName("should return tickets, when perform GET: /lotteries")
    void getAllTicketSuccess() throws Exception {
        List<String> tickets = Arrays.asList("123456", "654321", "000000");
        when(lotteryRepository.findTickets()).thenReturn(tickets);
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_TICKET_URL)
                        .content(stringify(request))
                        .headers(httpHeaders))
                .andExpect(jsonPath("$.tickets", is(tickets)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return [], when perform GET: /lotteries with no record in DB")
    void getEmptyTicketSuccess() throws Exception {
        List<String> tickets = List.of();
        when(lotteryRepository.findTickets()).thenReturn(tickets);
        mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_TICKET_URL)
                        .content(stringify(request))
                        .headers(httpHeaders))
                .andExpect(jsonPath("$.tickets", is(tickets)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return 500, when perform GET: /lotteries with retrieve DB error")
    void getTicketFailed() throws Exception {
        List<String> tickets = List.of();
        doThrow(new DataAccessException("Mocked data access exception") {
        }).when(lotteryRepository).findTickets();

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(GET_ALL_TICKET_URL)
                    .content(stringify(request))
                    .headers(httpHeaders));
        } catch (Exception ex) {
            assertException(HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }

    }
}