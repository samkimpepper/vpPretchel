package com.pretchel.pretchel0123jwt.modules.payments.message;

import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.gift.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.gift.repository.GiftRepository;
import com.pretchel.pretchel0123jwt.modules.notification.event.MessageCreatedEvent;
import com.pretchel.pretchel0123jwt.modules.payments.iamport.domain.IamportPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final GiftRepository giftRepository;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public void createMessage(IamportPayment payments) {
        Gift gift = payments.getGift();
        Message message = Message.builder()
                        .nickname(payments.getBuyerName())
                        .content(payments.getMessage())
                        .amount(payments.getAmount())
                        .payments(payments)
                        .gift(gift)
                        .build();

        messageRepository.save(message);

        // TODO: 여기서 Gift 도메인을 건드려도 되는 건가?????
        gift.pay(message.getAmount());
        if(gift.isGranterPrice()) {
            gift.changeState(GiftState.success);
        }

        // 알림 보냄
        Users receiver = gift.getEvent().getUsers();
        eventPublisher.publishEvent(new MessageCreatedEvent(message, gift, message.getNickname(), receiver));
    }
}
