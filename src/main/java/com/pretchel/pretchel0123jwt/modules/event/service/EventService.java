package com.pretchel.pretchel0123jwt.modules.event.service;

import com.pretchel.pretchel0123jwt.infra.config.jwt.JwtTokenProvider;
import com.pretchel.pretchel0123jwt.infra.global.exception.NotFoundException;
import com.pretchel.pretchel0123jwt.infra.global.exception.S3UploadException;
import com.pretchel.pretchel0123jwt.modules.event.domain.Event;
import com.pretchel.pretchel0123jwt.modules.account.domain.Users;
import com.pretchel.pretchel0123jwt.infra.util.S3Uploader;
import com.pretchel.pretchel0123jwt.modules.account.service.UsersService;
import com.pretchel.pretchel0123jwt.modules.event.dto.event.*;
import com.pretchel.pretchel0123jwt.infra.global.Response;
import com.pretchel.pretchel0123jwt.modules.event.dto.gift.GiftListDto;
import com.pretchel.pretchel0123jwt.modules.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository profileRepository;
    private final GiftService giftService;
    private final Response responseDto;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsersService usersService;
    private final S3Uploader s3Uploader;
    private final ModelMapper modelMapper;


    @Transactional
    public void save(EventCreateDto dto, String email) {
        Users users = usersService.findUserByEmail(email);

        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = new Date(sdf.parse(dto.getDeadLine()).getTime());
        } catch(ParseException ex) {
            ex.printStackTrace();
        }

        String profileImageUrl = null;
        String backgroundImageUrl = null;
        try {
            try {
                if(dto.imagesCount() > 0 && dto.getImages()[0] != null) {
                    profileImageUrl = s3Uploader.upload(dto.getImages()[0]);
                }
                if(dto.imagesCount() > 1 && dto.getImages()[1] != null) {
                    backgroundImageUrl = s3Uploader.upload(dto.getImages()[1]);
                }
            } catch(NullPointerException ex) {
                log.info("이벤트 생성 과정에서 프사 혹은 배사에서 널 포인터 발생. 근데 널이어도 상관 없음.");
            } finally {
                Event profile = Event.builder()
                        .nickname(dto.getNickName())
                        .eventType(dto.getEventType())
                        .profileImageUrl(profileImageUrl)
                        .backgroundImageUrl(backgroundImageUrl)
                        .deadLine(date)
                        .users(users)
                        .isExpired(false)
                        .build();
                profileRepository.save(profile);
            }
            return;

        } catch (IOException ex) {
            throw new S3UploadException();
        }
    }

    public List<EventListDto> getMyEvents(String email) {
        Users users = usersService.findUserByEmail(email);

//        List<EventMapping> profiles = profileRepository.findProfilesByUserId(users);
//        List<EventMapping> sortedProfiles = profiles.stream()
//                .sorted(Comparator.comparing(EventMapping::getCreateDate).reversed())
//                .collect(Collectors.toList());

        List<Event> eventList = profileRepository.findAllByUsers(users);
        List<EventListDto> dtoList = eventList.stream()
                .map(event -> {
                    return EventListDto.fromEvent(event);
                })
                .collect(Collectors.toList());

        return dtoList.stream()
                .sorted(Comparator.comparing(EventListDto::getCreateDate).reversed())
                .collect(Collectors.toList());
    }

    public ResponseEntity<?> getAllProfiles() {
        List<Event> profiles = profileRepository.findAll();

        return responseDto.success(profiles, "모든 프로필", HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> findAllByOrderByCreateDate(int pageNum, int profilesPerPage) {
        Page<Event> pages = profileRepository.findAll(
                PageRequest.of(pageNum - 1, profilesPerPage, Sort.by(Sort.Direction.DESC, "createDate"))
        );

//        List<ProfileResponseDto.View> sortedProfiles = profiles.stream()
//                .map(ProfileResponseDto.View::new)
//                .collect(Collectors.toList());

        List<Event> profiles = pages.getContent();
        List<ProfileResponseDto.View> profileDtos = profiles.stream()
                .map(profile -> modelMapper.map(profile, ProfileResponseDto.View.class))
                .collect(Collectors.toList());

        return responseDto.success(profileDtos, "모든프로필 근데 맵핑 안함 ㅠㅠ", HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public EventDetailDto getDetail(String eventId) {
        Event event = profileRepository.findById(eventId).orElseThrow(NotFoundException::new);
        List<GiftListDto> giftList = giftService.getMyGifts(event);
        return EventDetailDto.fromEvent(event, giftList);
    }


    public Event findById(String eventId) {
        return profileRepository.findById(eventId).orElseThrow(NotFoundException::new);
    }

    public Long count() {
        return profileRepository.count();
    }

    @Transactional
    public ResponseEntity<?> delete(String eventId) {
        Optional<Event> eventOptional = profileRepository.findById(eventId);
        if(eventOptional.isEmpty()) {
            return responseDto.fail("없는 이벤트임", HttpStatus.NOT_FOUND);
        }
        Event event = eventOptional.get();

        profileRepository.delete(event);

        return responseDto.success("삭제 완료");
    }
}
