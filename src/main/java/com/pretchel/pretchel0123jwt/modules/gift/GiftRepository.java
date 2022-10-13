package com.pretchel.pretchel0123jwt.modules.gift;

import com.pretchel.pretchel0123jwt.modules.event.domain.GiftState;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.event.domain.Gift;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GiftRepository extends JpaRepository<Gift, String> {

    @Query("select g from Gift g where g.event = ?1")
    List<Gift> findAllByEventId(Event event);

    @Query("select g from Gift g where g.state not in (:state1, :state2) and g.isProcessed = true")
    List<Gift> findAllByStateNotIn(@Param("state1") GiftState state1, @Param("state2") GiftState state2);

    Boolean existsByAddress(Address address);


}
