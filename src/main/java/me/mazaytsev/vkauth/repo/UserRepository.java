package me.mazaytsev.vkauth.repo;


import me.mazaytsev.vkauth.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByAccountId(String accountId);
}
