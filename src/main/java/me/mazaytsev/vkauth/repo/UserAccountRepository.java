package me.mazaytsev.vkauth.repo;


import me.mazaytsev.vkauth.domain.UserAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserAccountRepository extends CrudRepository<UserAccount, String> {
    Optional<UserAccount> findById(String id);
}
