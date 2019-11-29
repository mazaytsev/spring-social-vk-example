package me.mazaytsev.vkauth.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.mazaytsev.vkauth.domain.enums.OAuthProviderEnum;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    @Id
    String id;
    String displayName;
    String firstName;
    String lastName;
    String email;
    String userName;

    @Column(columnDefinition = "CLOB")
    String profileUrl;

    @Column(columnDefinition = "CLOB")
    String imageUrl;

    @Enumerated(EnumType.STRING)
    OAuthProviderEnum provider;
}
