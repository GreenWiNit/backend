// package com.example.integration.challenge;
//
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.example.green.domain.challenge.entity.PersonalChallenge;
// import com.example.green.domain.challenge.enums.ChallengeStatus;
// import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
// import com.example.green.domain.member.entity.Member;
// import com.example.green.domain.member.repository.MemberRepository;
// import com.example.green.domain.point.entity.vo.PointAmount;
// import com.example.integration.common.BaseIntegrationTest;
//
// @Transactional
// class ChallengeIntegrationTest extends BaseIntegrationTest {
//
//     @Autowired
//     private MockMvc mockMvc;
//
//     @Autowired
//     private PersonalChallengeRepository personalChallengeRepository;
//
//     @Autowired
//     private MemberRepository memberRepository;
//
//     private Member testMember;
//     private PersonalChallenge testChallenge;
//
//     @BeforeEach
//     void setUp() {
//         testMember = createAndSaveTestMember();
//         testChallenge = createAndSaveTestChallenge();
//     }
//
//     private Member createAndSaveTestMember() {
//         Member member = Member.create(
//             "testuser",
//             "테스트 사용자",
//             "test@example.com"
//         );
//         return memberRepository.save(member);
//     }
//
//     private PersonalChallenge createAndSaveTestChallenge() {
//         PersonalChallenge challenge = PersonalChallenge.create(
//             "TEST001",
//             "테스트 챌린지",
//             ChallengeStatus.PROCEEDING,
//             PointAmount.of(BigDecimal.valueOf(100)),
//             LocalDateTime.now(),
//             LocalDateTime.now().plusDays(7),
//             "image.jpg",
//             "챌린지 내용"
//         );
//         return personalChallengeRepository.save(challenge);
//     }
//
//     @Test
//     @WithMockUser(username = "testuser")
//     void 진행_중인_개인_챌린지_목록을_조회한다() throws Exception {
//         mockMvc.perform(get("/api/challenges/personal")
//                 .contentType(MediaType.APPLICATION_JSON))
//             .andDo(print())
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.success").value(true))
//             .andExpect(jsonPath("$.response.hasNext").value(false))
//             .andExpect(jsonPath("$.response.content[0].id").value(testChallenge.getId()))
//             .andExpect(jsonPath("$.response.content[0].challengeName").value(testChallenge.getChallengeName()))
//             .andExpect(jsonPath("$.error").doesNotExist());
//     }
//
//     @Test
//     @WithMockUser(username = "testuser")
//     void 존재하지_않는_챌린지를_조회하면_404_응답을_반환한다() throws Exception {
//         mockMvc.perform(get("/api/challenges/{challengeId}", 999L)
//                 .contentType(MediaType.APPLICATION_JSON))
//             .andDo(print())
//             .andExpect(status().isNotFound())
//             .andExpect(jsonPath("$.success").value(false))
//             .andExpect(jsonPath("$.error").exists());
//     }
//
//     @Test
//     @WithMockUser(username = "testuser")
//     void 로그인한_사용자가_챌린지를_조회하면_참여_상태를_함께_반환한다() throws Exception {
//         mockMvc.perform(get("/api/challenges/{challengeId}", testChallenge.getId())
//                 .contentType(MediaType.APPLICATION_JSON))
//             .andDo(print())
//             .andExpect(status().isOk())
//             .andExpect(jsonPath("$.success").value(true))
//             .andExpect(jsonPath("$.response.id").value(testChallenge.getId()))
//             .andExpect(jsonPath("$.response.title").value(testChallenge.getChallengeName()))
//             .andExpect(jsonPath("$.response.participationStatus").value("NOT_JOINED"))
//             .andExpect(jsonPath("$.error").doesNotExist());
//     }
// }