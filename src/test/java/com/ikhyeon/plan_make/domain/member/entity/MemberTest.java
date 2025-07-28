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
            Member member = createTestMember();
            member.incrementPlanCreationCount();
            member.incrementPlanCreationCount();
            member.incrementPlanCreationCount();

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
    }

    @DisplayName("계획 생성 가능 여부 테스트")
    @Nested
    class CanCreatePlanTodayTest {

        @Test
        @DisplayName("계획을 생성한 적이 없으면 생성 가능하다")
        void canCreatePlanToday_NeverCreated_ReturnsTrue() {
            // given
            Member member = new Member(null, SocialProvider.GOOGLE, "123456789", "test@example.com", 
                                     "테스트유저", "https://example.com/profile.jpg", 0, LocalDate.now().minusDays(1));

            // when & then
            assertThat(member.canCreatePlanToday()).isTrue();
        }

        @Test
        @DisplayName("오늘 2번 생성했으면 아직 생성 가능하다")
        void canCreatePlanToday_TwoTimesToday_ReturnsTrue() {
            // given
            Member member = new Member(null, SocialProvider.GOOGLE, "123456789", "test@example.com", 
                                     "테스트유저", "https://example.com/profile.jpg", 2, LocalDate.now());

            // when & then
            assertThat(member.canCreatePlanToday()).isTrue();
        }

        @Test
        @DisplayName("오늘 3번 생성했으면 더 이상 생성할 수 없다")
        void canCreatePlanToday_ThreeTimesToday_ReturnsFalse() {
            // given
            Member member = new Member(null, SocialProvider.GOOGLE, "123456789", "test@example.com", 
                                     "테스트유저", "https://example.com/profile.jpg", 3, LocalDate.now());

            // when & then
            assertThat(member.canCreatePlanToday()).isFalse();
        }

        @Test
        @DisplayName("어제 3번 생성했어도 오늘은 생성 가능하다")
        void canCreatePlanToday_ThreeTimesYesterday_ReturnsTrue() {
            // given
            Member member = new Member(null, SocialProvider.GOOGLE, "123456789", "test@example.com", 
                                     "테스트유저", "https://example.com/profile.jpg", 3, LocalDate.now().minusDays(1));

            // when & then
            assertThat(member.canCreatePlanToday()).isTrue();
        }
    }

    @DisplayName("일일 카운트 리셋 테스트")
    @Nested
    class ResetDailyPlanCountTest {

        @Test
        @DisplayName("canCreatePlanToday 호출 시 날짜가 다르면 카운트가 리셋된다")
        void canCreatePlanToday_DifferentDate_ResetsCount() {
            // given
            Member member = new Member(null, SocialProvider.GOOGLE, "123456789", "test@example.com", 
                                     "테스트유저", "https://example.com/profile.jpg", 3, LocalDate.now().minusDays(1));

            // when
            boolean canCreate = member.canCreatePlanToday();

            // then
            assertThat(canCreate).isTrue();
            assertThat(member.getDailyPlanCreationCount()).isEqualTo(0);
            assertThat(member.getLastPlanCreationDate()).isEqualTo(LocalDate.now());
        }

        @Test
        @DisplayName("incrementPlanCreationCount 호출 시 날짜가 다르면 카운트가 리셋된다")
        void incrementPlanCreationCount_DifferentDate_ResetsCount() {
            // given
            Member member = new Member(null, SocialProvider.GOOGLE, "123456789", "test@example.com", 
                                     "테스트유저", "https://example.com/profile.jpg", 3, LocalDate.now().minusDays(1));

            // when
            member.incrementPlanCreationCount();

            // then
            assertThat(member.getDailyPlanCreationCount()).isEqualTo(1);
            assertThat(member.getLastPlanCreationDate()).isEqualTo(LocalDate.now());
        }
    }
}