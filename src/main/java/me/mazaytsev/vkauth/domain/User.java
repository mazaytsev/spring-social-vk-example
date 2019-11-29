package me.mazaytsev.vkauth.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String firstName;
    String lastName;
    String accountId;

    @OneToOne
    @JoinColumn(name = "accountId", insertable = false, updatable = false)
    UserAccount account;
}
