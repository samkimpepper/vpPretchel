package com.pretchel.pretchel0123jwt.modules.gift;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpiredGiftRepository extends JpaRepository<CompletedGift, Long> {

}
