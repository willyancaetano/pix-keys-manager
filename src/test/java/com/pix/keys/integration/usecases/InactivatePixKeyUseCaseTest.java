package com.pix.keys.integration.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pix.keys.dto.AccountType;
import com.pix.keys.dto.InactivationPixKeyRequestDto;
import com.pix.keys.dto.KeyType;
import com.pix.keys.dto.PersonType;
import com.pix.keys.model.Account;
import com.pix.keys.model.PixKey;
import com.pix.keys.repository.AccountRepository;
import com.pix.keys.repository.PixKeyRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InactivatePixKeyUseCaseTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PixKeyRepository pixKeyRepository;

    @BeforeAll
    public void setUp() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @BeforeEach
    public void setUpForEachTest(){
        accountRepository.deleteAll();
        pixKeyRepository.deleteAll();
    }

    @DisplayName("Cenário feliz, onde a chave é inativada com sucesso")
    @Test
    public void shouldRunSuccessfully() throws Exception {

        Account newAccount = new Account(AccountType.CHECKING_ACCOUNT, 189, 60184, "Maria", "Claudia", PersonType.NATURAL_PERSON);
        accountRepository.save(newAccount);
        PixKey pixKey = pixKeyRepository.save(new PixKey(KeyType.EMAIL, "will@company.com", newAccount));

        mockMvc.perform(delete("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(
                                new InactivationPixKeyRequestDto(pixKey.getId())))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pixKey.getId()))
                .andExpect(jsonPath("$.type").value("EMAIL"))
                .andExpect(jsonPath("$.valueKey").value("will@company.com"))
                .andExpect(jsonPath("$.accountType").value("CHECKING_ACCOUNT"))
                .andExpect(jsonPath("$.branchNumber").value("189"))
                .andExpect(jsonPath("$.accountNumber").value("60184"))
                .andExpect(jsonPath("$.accountHolderName").value("Maria"))
                .andExpect(jsonPath("$.accountHolderSurname").value("Claudia"))
                .andExpect(jsonPath("$.creationDateTime").value(IsNull.notNullValue()))
                .andExpect(jsonPath("$.inactivationDateTime").value(IsNull.notNullValue()));

        assertTrue(accountRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findAll().size() == 1);

        Optional<PixKey> optionalPixKey = pixKeyRepository.findByValue("will@company.com");

        assertTrue(optionalPixKey.isPresent());

        PixKey pixKeyFounded = optionalPixKey.get();

        assertNotNull(pixKeyFounded.getInactivationDate());
        assertNotNull(pixKeyFounded.getInactivationTime());
        assertFalse(pixKeyFounded.isActive());
    }

    @DisplayName("Critério de aceite que determina impedir inativar uma chave que já está desativada")
    @Test
    public void shouldThrowAnErrorBecausePixKeyIsInactive() throws Exception {

        Account newAccount = new Account(AccountType.CHECKING_ACCOUNT, 189, 60184, "Maria", "Claudia", PersonType.NATURAL_PERSON);
        accountRepository.save(newAccount);
        PixKey pixKey = new PixKey(KeyType.CELLPHONE, "5511981056743", newAccount);
        pixKey.inactivateKey();
        pixKeyRepository.save(pixKey).getId();

        mockMvc.perform(delete("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(
                                new InactivationPixKeyRequestDto(pixKey.getId())))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Chave está inativada"));

        assertTrue(accountRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findByValue("5511981056743").isPresent());

        PixKey pixKeyFounded = pixKeyRepository.findByValue("5511981056743").get();
    }

    @DisplayName("Cenário de erro quando chave não existe")
    @Test
    public void shouldThrowAnErrorBecauseIdNotExists() throws Exception {

        Account newAccount = new Account(AccountType.CHECKING_ACCOUNT, 189, 60184, "Maria", "Claudia", PersonType.NATURAL_PERSON);
        accountRepository.save(newAccount);
        PixKey pixKey = new PixKey(KeyType.CELLPHONE, "5511981056743", newAccount);
        pixKey.inactivateKey();
        pixKeyRepository.save(pixKey).getId();

        mockMvc.perform(delete("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(
                                new InactivationPixKeyRequestDto(UUID.randomUUID().toString())))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Id da chave não existe"));

        assertTrue(accountRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findByValue("5511981056743").isPresent());

        PixKey pixKeyFounded = pixKeyRepository.findByValue("5511981056743").get();
    }
}
