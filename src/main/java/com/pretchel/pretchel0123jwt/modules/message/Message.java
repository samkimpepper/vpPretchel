package com.pretchel.pretchel0123jwt.modules.message;

import com.pretchel.pretchel0123jwt.infra.global.BaseTime;
import com.pretchel.pretchel0123jwt.modules.event.domain.Gift;
import com.pretchel.pretchel0123jwt.modules.gift.CompletedGift;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Message extends BaseTime {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name="uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int paid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="gift_id")
    private Gift gift;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="completed_gift_id")
    private CompletedGift completedGift;
}
