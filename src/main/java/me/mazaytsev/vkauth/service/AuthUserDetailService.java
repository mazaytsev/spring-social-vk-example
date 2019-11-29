package me.mazaytsev.vkauth.service;

import lombok.RequiredArgsConstructor;
import me.mazaytsev.vkauth.domain.User;
import me.mazaytsev.vkauth.domain.UserAccount;
import me.mazaytsev.vkauth.domain.enums.OAuthProviderEnum;
import me.mazaytsev.vkauth.repo.UserAccountRepository;
import me.mazaytsev.vkauth.repo.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
public class AuthUserDetailService implements SocialUserDetailsService, ConnectionSignUp {
    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public SocialUserDetails loadUserByUserId(String accountId) throws UsernameNotFoundException {
        User user = userRepository.findByAccountId(accountId).orElseThrow(() -> new UsernameNotFoundException(accountId));
        return new SocialUser(
                user.getAccount().getId(),
                "",
                true,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Override
    public String execute(Connection<?> connection) {
        UserProfile profile = connection.fetchUserProfile();
        return userRepository.findByAccountId(getProfileId(profile)).isPresent() ? null : registerUser(connection).getAccountId();
    }

    private User registerUser(Connection<?> connection) {


        UserProfile profile = connection.fetchUserProfile();
        UserAccount userAccount = userAccountRepository.save(
                UserAccount.builder()
                        .id(getProfileId(profile))
                        .email(profile.getEmail())
                        .userName(profile.getUsername())
                        .firstName(profile.getFirstName())
                        .lastName(profile.getLastName())
                        .imageUrl(connection.getImageUrl())
                        .profileUrl(connection.getProfileUrl())
                        .displayName(connection.getDisplayName())
                        .provider(OAuthProviderEnum.valueOf(connection.getKey().getProviderId().toUpperCase()))
                        .build()
        );
        User user = new User();
        user.setFirstName(profile.getFirstName());
        user.setLastName(profile.getLastName());
        user.setAccountId(userAccount.getId());
        return userRepository.save(user);
    }

    private String getProfileId(UserProfile profile) {
        if (hasText(profile.getId())) return profile.getId();
        if (hasText(profile.getEmail())) return profile.getEmail();
        if (hasText(profile.getUsername())) return profile.getUsername();

        throw new IllegalArgumentException("can't fetch user ID");
    }

}
