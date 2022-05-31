package com.pix.keys.model;

import com.pix.keys.dto.KeyType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "PixKey")
@Table(name = "TB_PIX_KEY")
public class PixKey {

    @Id
    @Column(name = "ID", updatable = false, unique = true, nullable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "PIX_KEY_TYPE", nullable = false, updatable = false)
    private KeyType type;

    @Column(name = "PIX_KEY_VALUE", nullable = false)
    private String value;

    @Column(name = "ACTIVE", nullable = false)
    private Boolean isActive;

    @Column(name = "CREATION_DATE", nullable = false, updatable = false)
    private LocalDate creationDate;

    @Column(name = "CREATION_TIME", nullable = false, updatable = false)
    private LocalTime creationTime;

    @Column(name = "INACTIVATION_DATE")
    private LocalDate inactivationDate;

    @Column(name = "INACTIVATION_TIME")
    private LocalTime inactivationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    public PixKey() {
    }

    public PixKey(KeyType type, String value, Account account) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.value = value;
        this.isActive = Boolean.TRUE;
        LocalDateTime now = LocalDateTime.now();
        this.creationDate = now.toLocalDate();
        this.creationTime = now.toLocalTime();
        this.account = account;
    }

    public String getId() {
        return id;
    }

    public KeyType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public LocalTime getCreationTime() {
        return creationTime;
    }

    public LocalDate getInactivationDate() {
        return inactivationDate;
    }

    public LocalTime getInactivationTime() {
        return inactivationTime;
    }

    public Account getAccount() {
        return account;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean isActive() {
        return isActive.equals(Boolean.TRUE);
    }

    public void inactivateKey() {
        this.isActive = Boolean.FALSE;
        LocalDateTime now = LocalDateTime.now();
        this.inactivationDate = now.toLocalDate();
        this.inactivationTime = now.toLocalTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PixKey pixKey = (PixKey) o;
        return id.equals(pixKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
