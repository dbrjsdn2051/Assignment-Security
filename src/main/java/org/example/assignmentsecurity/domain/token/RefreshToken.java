package org.example.assignmentsecurity.domain.token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private Long id;

    @Column(nullable = false)
    private String refreshToken ;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    @Builder
    public RefreshToken(Long id, String refreshToken, LocalDateTime expiryDate) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.expiryDate = expiryDate;
    }
}
