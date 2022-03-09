package com.pretchel.pretchel0123jwt.v1.service;

import com.pretchel.pretchel0123jwt.entity.Address;
import com.pretchel.pretchel0123jwt.entity.Users;
import com.pretchel.pretchel0123jwt.v1.dto.address.AddressRequestDto;
import com.pretchel.pretchel0123jwt.v1.dto.user.Response;
import com.pretchel.pretchel0123jwt.v1.repository.AddressRepository;
import com.pretchel.pretchel0123jwt.v1.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AddressService {
    private final AddressRepository addressRepository;
    private final UsersRepository usersRepository;
    private final Response responseDto;


}
