package com.pretchel.pretchel0123jwt.modules.event.controller;

import com.pretchel.pretchel0123jwt.infra.global.ResponseDto;
import com.pretchel.pretchel0123jwt.infra.util.Paginator;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventCreateDto;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventDetailDto;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.EventListDto;
import com.pretchel.pretchel0123jwt.infra.global.Response;
import com.pretchel.pretchel0123jwt.modules.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventApiController {

    private final EventService eventService;
    private final Response responseDto;

    private static final Integer PROFILES_PER_PAGE = 12;
    private static final Integer PAGES_PER_BLOCK = 5;

    @PostMapping
    public ResponseDto.Empty save(EventCreateDto dto) {
//        Enumeration params = request.getParameterNames();
//        System.out.println("--------------------------------");
//        while(params.hasMoreElements()) {
//            String name = (String)params.nextElement();
//            System.out.println(name + " : " +request.getParameter(name));
//        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        eventService.save(dto, email);
        return new ResponseDto.Empty();
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<?> getAllEvents(@PathVariable("page") Integer page) {
        log.info("페이지:" + page);

        try {
            Paginator paginator = new Paginator(PAGES_PER_BLOCK, PROFILES_PER_PAGE, eventService.count());
            Map<String, Object> pageInfo = paginator.getFixedBlock(page);
            for(String key : pageInfo.keySet()) {
                Object val = pageInfo.get(key);
                System.out.println(key + ": " + val);
            }
        } catch (IllegalStateException ex) {
            log.info("컨트롤러 페이지네이션 실패");
        }

        return eventService.findAllByOrderByCreateDate(page, PROFILES_PER_PAGE);
    }

    @GetMapping("/my")
    public ResponseDto.DataList<EventListDto> getMyEvents() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseDto.DataList<>(eventService.getMyEvents(email));
    }

    @GetMapping("/{id}")
    public ResponseDto.Data<EventDetailDto> getThisEvent(@PathVariable("id") String id) {
        return new ResponseDto.Data<>(eventService.getDetail(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable("id") String id) {
        return eventService.delete(id);
    }

//    @PostMapping("/test")
//    public void saveTest(ProfileRequestDto.Save save) {
//        log.info("닉네임:" + save.getNickName());
//        log.info("이벤타입:" + save.getEventType());
//        log.info(save.getImage().getOriginalFilename());
//    }
}
