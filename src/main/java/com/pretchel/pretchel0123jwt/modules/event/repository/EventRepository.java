package com.pretchel.pretchel0123jwt.modules.event.repository;

import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, String> {

    @Query("select p from Event p where p.users = ?1")
    List<EventMapping> findProfilesByUserId(Users users);

    List<Event> findAllByUsers(Users users);

    Page<Event> findAll(Pageable pageable);
}
