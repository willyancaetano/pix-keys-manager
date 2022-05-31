package com.pix.keys.repository;

import com.pix.keys.dto.AccountType;
import com.pix.keys.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountTypeAndBranchNumberAndAccountNumber(AccountType accountType, Integer branchNumber, Integer accountNumber);
}