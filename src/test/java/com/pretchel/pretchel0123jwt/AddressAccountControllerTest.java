package com.pretchel.pretchel0123jwt;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pretchel.pretchel0123jwt.config.WithMockCustomUser;
import com.pretchel.pretchel0123jwt.modules.info.dto.account.AccountCreateDto;
import com.pretchel.pretchel0123jwt.modules.info.dto.address.AddressCreateDto;
import com.pretchel.pretchel0123jwt.modules.account.dto.user.request.UserSignupDto;
import com.pretchel.pretchel0123jwt.modules.account.exception.UserAlreadyExistsException;
import com.pretchel.pretchel0123jwt.modules.account.service.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressAccountControllerTest {
    @Autowired
    private UsersService usersService;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    final String EMAIL = "duck12@gmail.com";

    @BeforeEach
    void setup() throws Exception {
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

        } catch (UserAlreadyExistsException ex) {

        }
    }

    @Test
    @WithMockCustomUser
    void createAddress() throws Exception {

        AddressCreateDto dto = AddressCreateDto.builder()
                .name("김둘기")
                .postCode("01234")
                .roadAddress("둘기로 1길 23")
                .detailAddress("비둘기아파트 404호")
                .phoneNum("01012345678")
                .isDefault(true)
                .build();
        String content = mapper.writeValueAsString(dto);

        mvc.perform(
                post("/api/address")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void createAccount() throws Exception {
        AccountCreateDto dto = AccountCreateDto.builder()
                .name("김둘기")
                .accountNum("1002255123456")
                .bank("우리")
                .bankCode("09")
                .birthday("이거왜필요한거지")
                .isDefault(true)
                .build();
        String content = mapper.writeValueAsString(dto);

        mvc.perform(post("/api/account")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockCustomUser
    void getMyAllAddresses() throws Exception {
        mvc.perform(get("/api/address"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].name", is("김둘기")));
    }
}
