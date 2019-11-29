package me.mazaytsev.vkauth.service;

import lombok.AllArgsConstructor;
import me.mazaytsev.vkauth.domain.UserAccount;
import me.mazaytsev.vkauth.repo.UserAccountRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {
    private final UserAccountRepository userAccountRepository;

    public UserAccount getUserAccountById(String id) {
        return userAccountRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(id));
    }
}
