package com.ikhyeon.plan_make.domain.member.entity;

import com.ikhyeon.plan_make.domain.member.vo.SocialProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Member 엔티티 테스트")
class MemberTest {

    @DisplayName("Member 생성 테스트")
    @Nested
    class CreateMemberTest {

        @Test
        @DisplayName("정상적으로 Member를 생성할 수 있다")
        void createMember_Success() {
            // given
            SocialProvider socialProvider = SocialProvider.GOOGLE;
            String socialId = "123456789";
            String email = "test@example.com";
            String name = "테스트유저";
            String profileImageUrl = "https://example.com/profile.jpg";

            // when
            Member member = new Member(null, socialProvider, socialId, email, name, profileImageUrl, 0, LocalDate.now().minusDays(1));

            // then
            assertThat(member.getSocialProvider()).isEqualTo(socialProvider);
            assertThat(member.getSocialId()).isEqualTo(socialId);
            assertThat(member.getEmail()).isEqualTo(email);
            assertThat(member.getName()).isEqualTo(name);
            assertThat(member.getProfileImageUrl()).isEqualTo(profileImageUrl);
            assertThat(member.getDailyPlanCreationCount()).isEqualTo(0);
        }
    }

    @DisplayName("일일 계획 생성 테스트")
    @Nested
    class DailyPlanCreationTest {

        private Member createTestMember() {
            return new Member(null, SocialProvider.GOOGLE, "123456789", "test@example.com", 
                            "테스트유저", "https://example.com/profile.jpg", 0, LocalDate.now().minusDays(1));
        }

        @Test
        @DisplayName("첫 번째 계획 생성 시 정상적으로 카운트가 증가한다")
        void incrementPlanCreationCount_FirstTime_Success() {
            // given
            Member member = createTestMember();

            // when
            member.incrementPlanCreationCount();

            // then
            assertThat(member.getDailyPlanCreationCount()).isEqualTo(1);
            assertThat(member.getLastPlanCreationDate()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("같은 날 3번째까지 계획 생성이 가능하다")
        void incrementPlanCreationCount_ThreeTimes_Success() {
            // given
            Member member = createTestMember();

            // when & then
            member.incrementPlanCreationCount();
            assertThat(member.getDailyPlanCreationCount()).isEqualTo(1);

            member.incrementPlanCreationCount();
            assertThat(member.getDailyPlanCreationCount()).isEqualTo(2);

            member.incrementPlanCreationCount();
            assertThat(member.getDailyPlanCreationCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("같은 날 4번째 계획 생성 시 예외가 발생한다")
        void incrementPlanCreationCount_FourthTime_ThrowsException() {
            // given
            Member member = new Member(null, SocialProvider.GOOGLE, "123456789", "test@example.com", 
                                     "테스트유저", "https://example.com/profile.jpg", 3, LocalDate.now());

            // when & then
            assertThatThrownBy(member::incrementPlanCreationCount)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("일일 계획 생성 한도를 초과했습니다.");
        }

        @Test
        @DisplayName("새로운 날에는 카운트가 리셋되어 다시 생성할 수 있다")
        void incrementPlanCreationCount_NewDay_ResetsCount() {
            // given
            Member member = new Member(null, SocialProvider.GOOGLE, "123456789", "test@example.com", 
                                     "테스트유저", "https://example.com/profile.jpg", 3, LocalDate.now().minusDays(1));

            // when
            member.incrementPlanCreationCount();

            // then
            assertThat(member.getDailyPlanCreationCount()).isEqualTo(1);
            assertThat(member.getLastPlanCreationDate()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("과거 날짜에서 카운트 리셋 후 첫 번째 생성")
        void incrementPlanCreationCount_AfterReset_FirstCreation() {
            // given
            Member member = new Member(null, SocialProvider.GOOGLE, "123456789", "test@example.com", 
                                     "테스트유저", "https://example.com/profile.jpg", 2, LocalDate.now().minusDays(2));

            // when
            member.incrementPlanCreationCount();

            // then
            assertThat(member.getDailyPlanCreationCount()).isEqualTo(1);
            assertThat(member.getLastPlanCreationDate()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("같은 날 연속 생성 시 카운트가 누적된다")
        void incrementPlanCreationCount_SameDay_Accumulates() {
            // given
            Member member = new Member(null, SocialProvider.GOOGLE, "123456789", "test@example.com", 
                                     "테스트유저", "https://example.com/profile.jpg", 1, LocalDate.now());
            
            // when
            member.incrementPlanCreationCount();
            
            // then
            assertThat(member.getDailyPlanCreationCount()).isEqualTo(2);
            assertThat(member.getLastPlanCreationDate()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("정확히 3개 생성된 상태에서 추가 생성 시 예외 발생")
        void incrementPlanCreationCount_ExactlyThree_ThrowsException() {
            // given
            Member member = new Member(null, SocialProvider.GOOGLE, "123456789", "test@example.com", 
                                     "테스트유저", "https://example.com/profile.jpg", 3, LocalDate.now());

            // when & then
            assertThatThrownBy(member::incrementPlanCreationCount)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("일일 계획 생성 한도를 초과했습니다.");
            
            // 상태가 변경되지 않았는지 확인
            assertThat(member.getDailyPlanCreationCount()).isEqualTo(3);
        }
    }
}