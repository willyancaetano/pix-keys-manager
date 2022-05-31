package com.pix.keys.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Optional;

@Builder
public class SearchPixKeyRequestDto{
    private String id;
    private KeyType keyType;
    private Integer branchNumber;
    private Integer accountNumber;
    private String accountHolderName;
    private LocalDate creationDate;
    private LocalDate inactivationDate;

    public String getId() {
        return id;
    }

    public KeyType getKeyType() {
        return keyType;
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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public LocalDate getInactivationDate() {
        return inactivationDate;
    }

    public boolean containsId() {
        return Optional.ofNullable(this.id).isPresent();
    }

    public boolean containsKeyType() {
        return Optional.ofNullable(this.keyType).isPresent();
    }

    public boolean containsBranchNumber() {
        return Optional.ofNullable(this.branchNumber).isPresent();
    }

    public boolean containsAccountNumber() {
        return Optional.ofNullable(this.accountNumber).isPresent();
    }

    public boolean containsAccountHolderName() {
        return Optional.ofNullable(this.accountHolderName).isPresent();
    }

    public boolean containsCreationDate() {
        return Optional.ofNullable(this.creationDate).isPresent();
    }

    public boolean containsInactivationDate() {
        return Optional.ofNullable(this.inactivationDate).isPresent();
    }

    public boolean containIdAndOthersFields() {
        if(containsId()) {
            if(containsKeyType() || containsBranchNumber() ||
                    containsAccountNumber() || containsAccountHolderName() ||
                    containsCreationDate() || containsInactivationDate()){
                return true;
            }
        }
        return false;
    }
}
