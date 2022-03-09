package com.pretchel.pretchel0123jwt.v1.service;

import com.pretchel.pretchel0123jwt.config.jwt.JwtTokenProvider;
import com.pretchel.pretchel0123jwt.entity.Profile;
import com.pretchel.pretchel0123jwt.entity.Users;
import com.pretchel.pretchel0123jwt.util.S3Uploader;
import com.pretchel.pretchel0123jwt.v1.dto.paging.PagingProfileDto;
import com.pretchel.pretchel0123jwt.v1.dto.profile.ProfileMapping;
import com.pretchel.pretchel0123jwt.v1.dto.profile.ProfileRequestDto;
import com.pretchel.pretchel0123jwt.v1.dto.profile.ProfileResponseDto;
import com.pretchel.pretchel0123jwt.v1.dto.user.Response;
import com.pretchel.pretchel0123jwt.v1.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final Response responseDto;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsersService usersService;
    private final S3Uploader s3Uploader;
    private final ModelMapper modelMapper;

    @Transactional
    public ResponseEntity<?> save(ProfileRequestDto.Save save, @AuthenticationPrincipal UserDetails userDetails) {
        Users users = usersService.getUsersByEmail(userDetails);
        if(users == null) {
            return responseDto.fail("존재하지 않는 이메일", HttpStatus.BAD_REQUEST);
        }

        String profileImageUrl = null;
        String backgroundImageUrl = null;
        try {
            try {
                if(save.getImages()[0] != null) {
                    profileImageUrl = s3Uploader.upload(save.getImages()[0]);
                }
                if(save.getImages()[1] != null) {
                    backgroundImageUrl = s3Uploader.upload(save.getImages()[1]);
                }
            } catch(NullPointerException ex) {
                System.out.println("널포인터 예외 ㅠㅠ");
            } finally {
                Profile profile = Profile.builder()
                        .nickname(save.getNickName())
                        .eventType(save.getEventType())
                        .profileImageUrl(profileImageUrl)
                        .backgroundImageUrl(backgroundImageUrl)
                        .users(users)
                        .build();
                profileRepository.save(profile);
            }
            return responseDto.success("프로필 생성 완료");

        } catch (IOException ex) {
            return responseDto.fail("프사 버킷에 저장 실패함 ㅠㅠ", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> getMyProfiles(@AuthenticationPrincipal UserDetails userDetails) {
        Users users = usersService.getUsersByEmail(userDetails);
        if(users == null) {
            return responseDto.fail("존재하지 않는 이메일", HttpStatus.BAD_REQUEST);
        }

        List<ProfileMapping> profiles = profileRepository.findProfilesByUserId(users);
        List<ProfileMapping> sortedProfiles = profiles.stream()
                .sorted(Comparator.comparing(ProfileMapping::getCreateDate).reversed())
                .collect(Collectors.toList());

        return responseDto.success(sortedProfiles, "보유 프로필들", HttpStatus.OK);
    }

    public ResponseEntity<?> getAllProfiles() {
        List<Profile> profiles = profileRepository.findAll();

        return responseDto.success(profiles, "모든 프로필", HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> findAllByOrderByCreateDate(int pageNum, int profilesPerPage) {
        Page<Profile> pages = profileRepository.findAll(
                PageRequest.of(pageNum - 1, profilesPerPage, Sort.by(Sort.Direction.DESC, "createDate"))
        );

//        List<ProfileResponseDto.View> sortedProfiles = profiles.stream()
//                .map(ProfileResponseDto.View::new)
//                .collect(Collectors.toList());

        List<Profile> profiles = pages.getContent();
        List<ProfileResponseDto.View> profileDtos = profiles.stream()
                .map(profile -> modelMapper.map(profile, ProfileResponseDto.View.class))
                .collect(Collectors.toList());

        return responseDto.success(profileDtos, "모든프로필 근데 맵핑 안함 ㅠㅠ", HttpStatus.OK);
    }

    public Long count() {
        return profileRepository.count();
    }
}
