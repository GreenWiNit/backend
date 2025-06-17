package com.example.green.domain.member.entity.vo;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {
    
    @Column(name = "nickname")
    private String nickname;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    public Profile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public boolean hasProfileImage() {
        return profileImageUrl != null && !profileImageUrl.isBlank();
    }

    public boolean isValidNickname() {
        return nickname != null && nickname.length() >= 2 && nickname.length() <= 20;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Profile profile = (Profile) o;
        return Objects.equals(nickname, profile.nickname) && 
               Objects.equals(profileImageUrl, profile.profileImageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, profileImageUrl);
    }
}