package com.kbtg.bootcamp.posttest.userTicket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.kbtg.bootcamp.posttest.Utils.assertException;
import static com.kbtg.bootcamp.posttest.Utils.stringify;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private final String GET_TICKET_BY_USER_URL = "/users/%s/lotteries";

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

    @Test
    @DisplayName("should return user ticket, when perform GET: /users/:userId/lotteries")
    void getTicketByUserSuccess() throws Exception {
        String userId = "0000011111";
        String url = String.format(GET_TICKET_BY_USER_URL, userId);

        List<UserTicketEntity> userTicketList = prepareUserTicketList(userId);
        when(userTicketRepository.findByUserId(anyString())).thenReturn(userTicketList);

        List<String> tickets = userTicketList.stream().map(e -> e.getLottery().getTicket()).collect(Collectors.toList());
        int count = userTicketList.size();
        int cost = 80 * count;
        GetUserTicketResponse getUserTicketResponse = new GetUserTicketResponse(tickets, count, cost);

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .headers(httpHeaders))
                .andExpect(content().json(stringify(getUserTicketResponse)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return 400 ticket, when perform GET: /users/:userId/lotteries with userId length < 10")
    void getTicketByUserFailedUserIdLengthLess() throws JsonProcessingException {
        String userId = "0";
        String url = String.format(GET_TICKET_BY_USER_URL, userId);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .headers(httpHeaders));
        } catch (Exception ex) {
            assertException(HttpStatus.BAD_REQUEST, ex);
        }
    }

    @Test
    @DisplayName("should return 400 ticket, when perform GET: /users/:userId/lotteries with userId length > 10")
    void getTicketByUserFailedUserIdLengthMore() throws JsonProcessingException {
        String userId = "00000111112";
        String url = String.format(GET_TICKET_BY_USER_URL, userId);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .headers(httpHeaders));
        } catch (Exception ex) {
            assertException(HttpStatus.BAD_REQUEST, ex);
        }
    }

    @Test
    @DisplayName("should return 404 ticket, when perform GET: /users/:userId/lotteries, with retrieve record not found")
    void getTicketByUserFailedRecordNotFound() throws Exception {
        String userId = "0000011111";
        String url = String.format(GET_TICKET_BY_USER_URL, userId);

        when(userTicketRepository.findByUserId(anyString())).thenReturn(null);

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .headers(httpHeaders));
            fail("Should throw exception");
        } catch (Exception ex) {
            assertException(HttpStatus.NOT_FOUND, ex);
        }
    }

    @Test
    @DisplayName("should return 500 ticket, when perform GET: /users/:userId/lotteries, with retrieve record error")
    void getTicketByUserFailedWhileRetrieveDB() throws Exception {
        String userId = "0000011111";
        String url = String.format(GET_TICKET_BY_USER_URL, userId);

        doThrow(new DataAccessException("Mocked data access exception") {
        }).when(userTicketRepository).findByUserId(anyString());

        try {
            mockMvc.perform(MockMvcRequestBuilders.get(url)
                    .headers(httpHeaders));
            fail("Should throw exception");
        } catch (Exception ex) {
            assertException(HttpStatus.INTERNAL_SERVER_ERROR, ex);
        }
    }

    public List<UserTicketEntity> prepareUserTicketList(String userId) throws JsonProcessingException {
        LotteryEntity lottery = new LotteryEntity();
        lottery.setId(1);
        lottery.setTicket("111111");
        lottery.setPrice(80);
        lottery.setAmount(1);

        List<LotteryEntity> lotteryList = new ArrayList<>(List.of());
        lotteryList.add((LotteryEntity) cloneObject(lottery, LotteryEntity.class));
        lotteryList.add((LotteryEntity) cloneObject(lottery, LotteryEntity.class));
        lotteryList.add((LotteryEntity) cloneObject(lottery, LotteryEntity.class));
        lotteryList.get(1).setTicket("222222");
        lotteryList.get(2).setTicket("333333");

        UserTicketEntity userTicket = new UserTicketEntity();
        userTicket.setId(1);
        userTicket.setUserId(userId);
        userTicket.setLottery(lotteryList.get(0));

        List<UserTicketEntity> userTicketList = new ArrayList<>(List.of());
        userTicketList.add((UserTicketEntity) cloneObject(userTicket, UserTicketEntity.class));
        userTicketList.add((UserTicketEntity) cloneObject(userTicket, UserTicketEntity.class));
        userTicketList.add((UserTicketEntity) cloneObject(userTicket, UserTicketEntity.class));
        userTicketList.get(1).setLottery(lotteryList.get(1));
        userTicketList.get(2).setLottery(lotteryList.get(2));

        return userTicketList;
    }

    public Object cloneObject(Object obj, Class to) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String str = objectMapper.writeValueAsString(obj);

        return objectMapper.readValue(str, to);
    }

}