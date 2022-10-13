package com.pretchel.pretchel0123jwt.modules.event.domain;

import com.pretchel.pretchel0123jwt.infra.global.BaseTime;
import com.pretchel.pretchel0123jwt.modules.info.domain.Account;
import com.pretchel.pretchel0123jwt.modules.info.domain.Address;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/*
* 선물이름
* 프로필 외래키
* 가격
* 퍼센티지(이름 이걸로 해야되나. 프로그레스로 할까?
* 사진
* 링크?
*
* */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Gift extends BaseTime {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(length = 36)
    private String id;

    @Column
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private Date deadLine;

    @Column
    private int funded;

    @Column
    private String giftImageUrl;

    @Column(nullable = false)
    private String link;

    @Column(nullable = false)
    private String story;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="address_id", nullable = false)
    private Address address;

    // TODO: default 적용 어떻게 하지? 일단 빌더에서 초기화하도록 함
    @Enumerated(EnumType.STRING)
    private GiftState state;

    @Column
    private boolean isProcessed;

    public void changeState(GiftState state, boolean isProcessed) {
        this.state = state;
        this.isProcessed = isProcessed;
    }

    public void pay(int paid) {
        //if(funded + paid < price) TODO: 초과하면 어떻게 되는지 상의 안 했음
        funded += paid;
    }

    public boolean isGraterthanPrice() {
        return (funded >= price);
    }
}
