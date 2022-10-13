package com.pretchel.pretchel0123jwt.modules.message;

import com.pretchel.pretchel0123jwt.infra.global.ResponseDto;
import com.pretchel.pretchel0123jwt.infra.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.modules.event.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.message.dto.MessageCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/message")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;
    private final GiftRepository giftRepository;

    @PostMapping
    public ResponseDto.Empty create(@RequestBody MessageCreateDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        messageService.save(dto);
        return new ResponseDto.Empty();
    }
}
