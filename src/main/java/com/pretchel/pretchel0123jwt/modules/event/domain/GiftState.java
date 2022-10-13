package com.pretchel.pretchel0123jwt.modules.event.domain;

/*
* ongoing: 마감기한 안 지남 + 돈 안 참
* completed: 마감기한 안 지남 + 돈 참
* expired: 마감기한 지남 + 돈 안 참
* finished: 마감기한 안 지남 + 돈 안 참. 근데 사용자가 마감함. (일종의, 강제적 expired임)
*   finished는 그냥 없애는게 나을 것 같아 도메인에서는.
* canceled: 사용자가 취소함
*
* 즉, 사용자가 요청할 수 있는 건 finish와 cancel
* */

public enum GiftState {
    ongoing,
    completed,
    expired,
    canceled
}
