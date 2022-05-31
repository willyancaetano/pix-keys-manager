package com.pix.keys.integration.usecases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pix.keys.dto.AccountType;
import com.pix.keys.dto.KeyType;
import com.pix.keys.dto.PersonType;
import com.pix.keys.dto.PixKeyUpdateValueRequestDto;
import com.pix.keys.model.Account;
import com.pix.keys.model.PixKey;
import com.pix.keys.repository.AccountRepository;
import com.pix.keys.repository.PixKeyRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UpdatePixKeyUseCaseTest {

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

    @DisplayName("Critério de aceite que informa que deve ser validado se chave está conforme seu tipo determinado")
    @Test
    public void shouldThrowExceptionBecauseIncorrectFormat() throws Exception {

        Account newAccount = new Account(AccountType.CHECKING_ACCOUNT, 189, 60184, "Maria", "Claudia", PersonType.NATURAL_PERSON);
        accountRepository.save(newAccount);
        String id = pixKeyRepository.save(new PixKey(KeyType.EMAIL, "will@company.com", newAccount)).getId();

        mockMvc.perform(patch("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(
                                new PixKeyUpdateValueRequestDto(
                                        UUID.fromString(id),UUID.randomUUID().toString(),
                                        newAccount.getAccountType(), newAccount.getBranchNumber(),
                                        newAccount.getAccountNumber(), newAccount.getAccountHolderName(),
                                        newAccount.getAccountHolderSurname())))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Email inválido"));

        assertTrue(accountRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findByValue("will@company.com").isPresent());
    }

    @DisplayName("Critério de aceite que determina impedir atualização de chave inativada")
    @Test
    public void shouldThrowAnErrorBecausePixKeyIsInactive() throws Exception {

        Account newAccount = new Account(AccountType.CHECKING_ACCOUNT, 189, 60184, "Maria", "Claudia", PersonType.NATURAL_PERSON);
        accountRepository.save(newAccount);
        PixKey pixKey = new PixKey(KeyType.CELLPHONE, "5511981056743", newAccount);
        pixKey.disableKey();
        pixKeyRepository.save(pixKey).getId();

        mockMvc.perform(patch("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(
                                new PixKeyUpdateValueRequestDto(
                                        UUID.fromString(pixKey.getId()),"5527999823456",
                                        newAccount.getAccountType(), newAccount.getBranchNumber(),
                                        newAccount.getAccountNumber(), newAccount.getAccountHolderName(),
                                        newAccount.getAccountHolderSurname())))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Chave está inativada"));

        assertTrue(accountRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findByValue("5511981056743").isPresent());
    }

    @DisplayName("Critério de aceite que informa que deve retornar 200 quando alteração for realizada com sucesso")
    @Test
    public void shouldRunSuccessfully() throws Exception {

        Account newAccount = new Account(AccountType.CHECKING_ACCOUNT, 189, 60184, "Maria", "Claudia", PersonType.NATURAL_PERSON);
        accountRepository.save(newAccount);
        String id = pixKeyRepository.save(new PixKey(KeyType.CELLPHONE, "+5571983450924", newAccount)).getId();

        mockMvc.perform(patch("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(
                                new PixKeyUpdateValueRequestDto(
                                        UUID.fromString(id),"+5561983450924",
                                        newAccount.getAccountType(), newAccount.getBranchNumber(),
                                        newAccount.getAccountNumber(), newAccount.getAccountHolderName(),
                                        newAccount.getAccountHolderSurname())))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.type").value("CELLPHONE"))
                .andExpect(jsonPath("$.valueKey").value("+5561983450924"));

        Optional<Account> optionalAccount = accountRepository.findByAccountTypeAndBranchNumberAndAccountNumber(AccountType.CHECKING_ACCOUNT, 189, 60184);
        assertTrue(optionalAccount.isPresent());

        Account account = optionalAccount.get();

        assertEquals("Maria", account.getAccountHolderName());
        assertEquals("Claudia", account.getAccountHolderSurname());
        assertEquals(PersonType.NATURAL_PERSON, account.getPersonType());
        assertEquals(1, account.getKeys().size());

        Optional<PixKey> optionalPixKey = pixKeyRepository.findById(id);
        assertTrue(optionalPixKey.isPresent());

        PixKey pixKey = optionalPixKey.get();
        assertEquals(KeyType.CELLPHONE, pixKey.getType());
        assertEquals("+5561983450924", pixKey.getValue());
        assertTrue(pixKey.isActive());
    }

    @DisplayName("Critério de aceite que informa que deve retornar 422 quando campos não respeitarem regra de validação")
    @Test
    public void shouldThrowValidationException() throws Exception {

        Account account = new Account(AccountType.CHECKING_ACCOUNT, 2, 58964, "João", "Silveira", PersonType.NATURAL_PERSON);
        accountRepository.save(account);

        PixKey pixKey = new PixKey(KeyType.RANDOM, "1d1d6467-e939-46e0-94e6-9f5298fb5e40", account);
        pixKeyRepository.save(pixKey);

        mockMvc.perform(patch("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "id": "fdd919ab-198c-4fc0-91ac-195a2a2d96c4",
                                        "valueKey": "1d1d6467-e939-46e0-94e6-9f5298fb5e40",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 12345,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Validation error"));

        assertTrue(accountRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findByValue("1d1d6467-e939-46e0-94e6-9f5298fb5e40").isPresent());
    }


    @DisplayName("Critério de aceite que informa que deve retornar 404 quando ID não for encontrado")
    @Test
    public void shouldThrowExceptionBecauseIdDoesNotExist() throws Exception {

        Account account = new Account(AccountType.CHECKING_ACCOUNT, 2, 58964, "João", "Silveira", PersonType.NATURAL_PERSON);
        accountRepository.save(account);

        PixKey pixKey = new PixKey(KeyType.RANDOM, "1d1d6467-e939-46e0-94e6-9f5298fb5e40", account);
        pixKeyRepository.save(pixKey);

        mockMvc.perform(patch("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "id": "fdd919ab-198c-4fc0-91ac-195a2a2d96c4",
                                        "valueKey": "1d1d6467-e939-46e0-94e6-9f5298fb5e40",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Id da chave não existe"));

        assertTrue(accountRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findByValue("1d1d6467-e939-46e0-94e6-9f5298fb5e40").isPresent());
    }

    @DisplayName("Valida se regra que impede atualização de chave aleatória está correta")
    @Test
    public void shouldThrowAnErrorBecauseIsNotAllowedUpdateRandomKey() throws Exception {

        Account newAccount = new Account(AccountType.CHECKING_ACCOUNT, 189, 60184, "Maria", "Claudia", PersonType.NATURAL_PERSON);
        accountRepository.save(newAccount);
        String value = UUID.randomUUID().toString();
        String id = pixKeyRepository.save(new PixKey(KeyType.RANDOM, value, newAccount)).getId();

        mockMvc.perform(patch("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(
                                new PixKeyUpdateValueRequestDto(
                                        UUID.fromString(id),UUID.randomUUID().toString(),
                                        newAccount.getAccountType(), newAccount.getBranchNumber(),
                                        newAccount.getAccountNumber(), newAccount.getAccountHolderName(),
                                        newAccount.getAccountHolderSurname())))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Tipo de chave não permitido a alteração"));

        assertTrue(accountRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findByValue(value).isPresent());
    }
}
