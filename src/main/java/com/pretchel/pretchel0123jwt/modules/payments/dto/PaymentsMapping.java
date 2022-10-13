package com.pretchel.pretchel0123jwt.modules.payments.dto;

import java.time.LocalDateTime;

public interface PaymentsMapping {
    String getBuyerName();
    int getAmount();
    String getMessage();
    LocalDateTime getPaidAt();
}
