package com.pretchel.pretchel0123jwt.modules.info.repository;

import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.modules.info.dto.address.AddressMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, String> {

    List<Address> findAllByUsers(Users users);

    @Query("select a from Address a where a.users = ?1")
    List<AddressMapping> findAllByUserId(Users users);
}
