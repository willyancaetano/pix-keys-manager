package com.pix.keys.model;

import com.pix.keys.dto.AccountType;
import com.pix.keys.dto.PersonType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity(name = "Account")
@Table(name = "TB_ACCOUNT")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false, unique = true)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "ACCOUNT_TYPE", nullable = false)
    private AccountType accountType;

    @Column(name = "BRANCH_NUMBER", nullable = false)
    private Integer branchNumber;

    @Column(name = "ACCOUNT_NUMBER", nullable = false)
    private Integer accountNumber;

    @Column(name = "HOLDER_NAME", nullable = false)
    private String accountHolderName;

    @Column(name = "HOLDER_SURNAME", nullable = true)
    private String accountHolderSurname;

    //TODO depende de questionamento
    @Enumerated(EnumType.STRING)
    @Column(name = "PERSON_TYPE", nullable = false)
    private PersonType personType;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PixKey> keys;

    public Account() {
    }

    public Account(AccountType accountType, Integer branchNumber, Integer accountNumber, String accountHolderName, String accountHolderSurname, PersonType personType) {
        this.accountType = accountType;
        this.branchNumber = branchNumber;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.accountHolderSurname = accountHolderSurname;
        this.personType = personType;
        this.keys = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public Integer getBranchNumber() {
        return branchNumber;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getAccountHolderSurname() {
        return accountHolderSurname;
    }

    public List<PixKey> getKeys() {
        return Collections.unmodifiableList(keys);
    }

    public void addKey(PixKey key) {
        this.keys.add(key);
    }

    public PersonType getPersonType() {
        return personType;
    }

    public boolean isJuridicalPerson() {
        return this.personType == PersonType.JURIDICAL_PERSON;
    }

    public boolean isNaturalPerson() {
        return this.personType == PersonType.NATURAL_PERSON;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id.equals(account.id) && accountType == account.accountType && branchNumber.equals(account.branchNumber) && accountNumber.equals(account.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountType, branchNumber, accountNumber);
    }
}
