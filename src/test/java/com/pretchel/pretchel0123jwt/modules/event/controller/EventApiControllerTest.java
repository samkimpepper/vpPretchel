package com.pretchel.pretchel0123jwt.modules.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.config.WithMockCustomUser;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UserSignupDto;
import com.pretchel.pretchel0123jwt.modules.account.exception.UserAlreadyExistsException;
import com.pretchel.pretchel0123jwt.modules.account.repository.UsersRepository;
import com.pretchel.pretchel0123jwt.modules.account.service.UsersService;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventListDto;
import com.pretchel.pretchel0123jwt.modules.event.dto.gift.GiftCreateDto;
import com.pretchel.pretchel0123jwt.modules.info.repository.AccountRepository;
import com.pretchel.pretchel0123jwt.modules.info.repository.AddressRepository;
import com.pretchel.pretchel0123jwt.modules.event.service.EventService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EventApiControllerTest {

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private EventService eventService;


    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    Users user;
    String eventId, accountId, addressId;


    @BeforeEach
    void setup() throws Exception {
        // given
        UserSignupDto dto = UserSignupDto.builder()
                .email("duck12@gmail.com")
                .password("password")
                .checkPassword("password")
                .birthday("2022-08-27")
                .gender("FEMALE")
                .phoneNumber("01012345678")
                .build();

        try {
            usersService.signUp(dto);
            user = usersService.findUserByEmail("duck12@gmail.com");


        } catch (UserAlreadyExistsException ex) {

        }

    }

    @AfterEach
    void clean() {
        usersRepository.deleteAll();
    }

    @Test
    @WithMockCustomUser
    void createEvent() throws Exception {
        MockMultipartFile[] images = new MockMultipartFile[2];
        images[0] = mockMultipartUpload();

        // 이렇게하면 안 됨
//        mvc.perform(post("/api/event")
//                .content(content)
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .accept(MediaType.APPLICATION_JSON)
//        ).andDo(print()).andExpect(status().isOk());

        mvc.perform(
                multipart("/api/event")
                        .file(images[0])
                        .param("nickName", "시발")
                        .param("eventType", "생일")
                        .param("deadLine", "2022-09-21")
        ).andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @WithMockCustomUser
    void createGift() throws Exception {
        List<EventListDto> eventList = eventService.getMyEvents("duck12@gmail.com");
        eventId = eventList.get(0).getId();
        user = usersRepository.findByEmail("duck12@gmail.com").orElseThrow();

        Account account = Account.builder()
        .name("김둘기")
        .accountNum("1002255123456")
        .bank("우리")
        .bankCode("09")
        .birthday("이거왜필요한거지")
        .users(user)
        .build();

        Address address = Address.builder()
                .name("김둘기")
                .postCode("01234")
                .roadAddress("둘기로 1길 23")
                .detailAddress("비둘기아파트 404호")
                .phoneNum("01012345678")
                .isDefault(true)
                .users(user)
                .build();

        accountId = accountRepository.save(account).getId();
        addressId = addressRepository.save(address).getId();
        user.setDefaultAccount(account);
        user.setDefaultAddress(address);
        usersRepository.save(user);

        GiftCreateDto dto = GiftCreateDto.builder()
                .eventId(eventId)
                .accountId(accountId)
                .addressId(addressId)
                .name("애플펜슬")
                .price(100000)
                .link("www.naver.com")
                .story("필기할 때 요긴하게 쓰려고 합니다.")
                .build();
        String content = mapper.writeValueAsString(dto);

        mvc.perform(
                post("/api/gift")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void getMyEvents() throws Exception {
        mvc.perform(
                get("/api/event/my"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].nickname", is("시발")));

    }

    // 실패해야됨. address가 gift에 속해있어서..
    @Test
    @WithMockCustomUser
    void deleteAddress() throws Exception {
        Users user = usersRepository.findByEmail("duck12@gmail.com").orElseThrow();
        String addressId = user.getDefaultAddress().getId();

        mvc.perform(
                delete("/api/address/" + addressId)
        ).andDo(print())
        .andExpect(status().isBadRequest());
    }


    // MultipartFile 객체 만드는 로직 ===================================================================================
    MockMultipartFile mockMultipartUpload() throws IOException {
        String fileName = "fluffy-cow";
        String contentType = "jpg";
        String filePath = "src/test/resources/images/fluffy-cow.jpg";
        MockMultipartFile mockFile = getMockMultipartFile(fileName, contentType, filePath);

        String getFileName = mockFile.getOriginalFilename().toLowerCase();
        return mockFile;

//        MatcherAssert.assertThat(getFileName, is(fileName.toLowerCase() + "." + contentType));
//        System.out.println(getFileName);
//        System.out.println(fileName);
    }

    MockMultipartFile getMockMultipartFile(String fileName, String contentType, String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(new File(path));
        return new MockMultipartFile(fileName, fileName + "." + contentType, contentType, fileInputStream);
    }
}