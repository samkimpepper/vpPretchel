package com.pretchel.pretchel0123jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private Users users;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String accountNum;

    @Column(nullable = false)
    private String bank;

    @Column(nullable = false)
    private String bankCode;

    @Column(nullable = false)
    private String birthday;

}
