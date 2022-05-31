package com.pix.keys.integration.usecases;

import com.pix.keys.dto.AccountType;
import com.pix.keys.dto.KeyType;
import com.pix.keys.dto.PersonType;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GeneratePixKeyUseCaseTest {

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

    @DisplayName("Cenário feliz, cria conta e chave pix")
    @Test
    public void shouldCreateAccountAndPixKey() throws Exception {
        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                        """
                            {
                                "type": "RANDOM",
                                "valueKey": "1d1d6467-e939-46e0-94e6-9f5298fb5e40",
                                "accountType": "CHECKING_ACCOUNT",
                                "branchNumber": 2,
                                "accountNumber": 58964,
                                "accountHolderName": "João",
                                "accountHolderSurname": "Silveira",
                                "personType": "JURIDICAL_PERSON"
                            }
                        """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        Optional<Account> optionalAccount = accountRepository.findByAccountTypeAndBranchNumberAndAccountNumber(AccountType.CHECKING_ACCOUNT, 2, 58964);
        assertTrue(optionalAccount.isPresent());

        Account account = optionalAccount.get();

        assertEquals("João", account.getAccountHolderName());
        assertEquals("Silveira", account.getAccountHolderSurname());
        assertEquals(PersonType.JURIDICAL_PERSON, account.getPersonType());
        assertEquals(1, account.getKeys().size());

        Optional<PixKey> optionalPixKey = pixKeyRepository.findByValue("1d1d6467-e939-46e0-94e6-9f5298fb5e40");
        assertTrue(optionalPixKey.isPresent());

        PixKey pixKey = optionalPixKey.get();
        assertEquals(KeyType.RANDOM, pixKey.getType());
        assertEquals("1d1d6467-e939-46e0-94e6-9f5298fb5e40", pixKey.getValue());
        assertNotNull(pixKey.getCreationDate());
        assertNotNull(pixKey.getCreationTime());
        assertTrue(pixKey.isActive());
    }

    @DisplayName("Cenário onde a conta já existe e apenas a chave é criada")
    @Test
    public void shouldCreateOnlyPixKeyBecauseAccountAlreadyExists() throws Exception {

        Account newAccount = new Account(AccountType.CHECKING_ACCOUNT, 2, 58964, "João", "Silveira", PersonType.JURIDICAL_PERSON);
        accountRepository.save(newAccount);

        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "RANDOM",
                                        "valueKey": "1d1d6467-e939-46e0-94e6-9f5298fb5e40",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira",
                                        "personType": "JURIDICAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        Optional<Account> optionalAccount = accountRepository.findByAccountTypeAndBranchNumberAndAccountNumber(AccountType.CHECKING_ACCOUNT, 2, 58964);
        assertTrue(optionalAccount.isPresent());

        Account account = optionalAccount.get();

        assertEquals("João", account.getAccountHolderName());
        assertEquals("Silveira", account.getAccountHolderSurname());
        assertEquals(PersonType.JURIDICAL_PERSON, account.getPersonType());
        assertEquals(1, account.getKeys().size());

        Optional<PixKey> optionalPixKey = pixKeyRepository.findByValue("1d1d6467-e939-46e0-94e6-9f5298fb5e40");
        assertTrue(optionalPixKey.isPresent());

        PixKey pixKey = optionalPixKey.get();
        assertEquals(KeyType.RANDOM, pixKey.getType());
        assertEquals("1d1d6467-e939-46e0-94e6-9f5298fb5e40", pixKey.getValue());
        assertTrue(pixKey.isActive());
    }

    @DisplayName("Critério de aceite que barra limite de chaves para pessoa física, atual 5 o valor")
    @Test
    public void shouldThrowAnErrorBecauseLimitKeysExceededByAccountNaturalPerson() throws Exception {

        Account newAccount = new Account(AccountType.CHECKING_ACCOUNT, 2, 58964, "Claudia", "Beatriz", PersonType.NATURAL_PERSON);
        accountRepository.save(newAccount);

        for(int i = 0; i < 5; i++) {
            pixKeyRepository.save(new PixKey(KeyType.RANDOM, UUID.randomUUID().toString(), newAccount));
        }

        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "RANDOM",
                                        "valueKey": "1d1d6467-e939-46e0-94e6-9f5298fb5e40",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "Claudia",
                                        "accountHolderSurname": "Beatriz",
                                        "personType": "NATURAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Foi excedido a quantidade máxima de chaves para pessoa física"));

        Optional<Account> optionalAccount = accountRepository.findByAccountTypeAndBranchNumberAndAccountNumber(AccountType.CHECKING_ACCOUNT, 2, 58964);
        assertTrue(optionalAccount.isPresent());

        Account account = optionalAccount.get();

        assertEquals("Claudia", account.getAccountHolderName());
        assertEquals("Beatriz", account.getAccountHolderSurname());
        assertEquals(PersonType.NATURAL_PERSON, account.getPersonType());
        assertEquals(5, account.getKeys().size());
    }

    @DisplayName("Critério de aceite que barra limite de chaves para pessoa jurídica, atual 20 o valor")
    @Test
    public void shouldThrowAnErrorBecauseLimitKeysExceededByAccountJuridicalPerson() throws Exception {

        Account newAccount = new Account(AccountType.CHECKING_ACCOUNT, 2, 58964, "Claudia", "Beatriz", PersonType.JURIDICAL_PERSON);
        accountRepository.save(newAccount);

        for(int i = 0; i < 20; i++) {
            pixKeyRepository.save(new PixKey(KeyType.RANDOM, UUID.randomUUID().toString(), newAccount));
        }

        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "RANDOM",
                                        "valueKey": "1d1d6467-e939-46e0-94e6-9f5298fb5e40",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "Claudia",
                                        "accountHolderSurname": "Beatriz",
                                        "personType": "JURIDICAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Foi excedido a quantidade máxima de chaves para pessoa jurídica"));

        Optional<Account> optionalAccount = accountRepository.findByAccountTypeAndBranchNumberAndAccountNumber(AccountType.CHECKING_ACCOUNT, 2, 58964);
        assertTrue(optionalAccount.isPresent());

        Account account = optionalAccount.get();

        assertEquals("Claudia", account.getAccountHolderName());
        assertEquals("Beatriz", account.getAccountHolderSurname());
        assertEquals(PersonType.JURIDICAL_PERSON, account.getPersonType());
        assertEquals(20, account.getKeys().size());
    }

    @DisplayName("Critério de aceite que informa que chaves não podem ter valores duplicados")
    @Test
    public void shouldThrowAnErrorPixKeyAlreadyExists() throws Exception {

        Account account = new Account(AccountType.CHECKING_ACCOUNT, 1, 12345, "Claudia", "Beatriz", PersonType.NATURAL_PERSON);
        accountRepository.save(account);

        PixKey pixKey = new PixKey(KeyType.RANDOM, "1d1d6467-e939-46e0-94e6-9f5298fb5e40", account);
        pixKeyRepository.save(pixKey);

        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "RANDOM",
                                        "valueKey": "1d1d6467-e939-46e0-94e6-9f5298fb5e40",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira",
                                        "personType": "JURIDICAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Chave pix já existe"));

        assertTrue(accountRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findAll().size() == 1);
        assertTrue(pixKeyRepository.findByValue("1d1d6467-e939-46e0-94e6-9f5298fb5e40").isPresent());
    }

    //Validação
    @DisplayName("Valida regra de validação de UUID, chave aleatória")
    @Test
    public void shouldThrowAnErrorBecauseRandomKeyIsInvalid() throws Exception {
        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "RANDOM",
                                        "valueKey": "11",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira",
                                        "personType": "JURIDICAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Chave aleatória inválida"));

        assertTrue(pixKeyRepository.findAll().size() == 0);
        assertTrue(accountRepository.findAll().size() == 0);
    }

    @DisplayName("Valida regra de validação de email")
    @Test
    public void shouldThrowAnErrorBecauseEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "EMAIL",
                                        "valueKey": "willyanGmail.com",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira",
                                        "personType": "JURIDICAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Email inválido"));

        assertTrue(pixKeyRepository.findAll().size() == 0);
        assertTrue(accountRepository.findAll().size() == 0);
    }

    @DisplayName("Verifica se regra de validação de Email está correta - cenário ok")
    @Test
    public void shouldRunSuccessBecauseEmailIsValid() throws Exception {
        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "EMAIL",
                                        "valueKey": "user@provider.com",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira",
                                        "personType": "JURIDICAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        Optional<Account> optionalAccount = accountRepository.findByAccountTypeAndBranchNumberAndAccountNumber(AccountType.CHECKING_ACCOUNT, 2, 58964);
        assertTrue(optionalAccount.isPresent());

        Account account = optionalAccount.get();

        assertEquals("João", account.getAccountHolderName());
        assertEquals("Silveira", account.getAccountHolderSurname());
        assertEquals(PersonType.JURIDICAL_PERSON, account.getPersonType());
        assertEquals(1, account.getKeys().size());

        Optional<PixKey> optionalPixKey = pixKeyRepository.findByValue("user@provider.com");
        assertTrue(optionalPixKey.isPresent());

        PixKey pixKey = optionalPixKey.get();
        assertEquals(KeyType.EMAIL, pixKey.getType());
        assertEquals("user@provider.com", pixKey.getValue());
        assertNotNull(pixKey.getCreationDate());
        assertNotNull(pixKey.getCreationTime());
        assertTrue(pixKey.isActive());
    }

    @DisplayName("Verifica se regra de validação de CPF está correta - cenário de erro")
    @Test
    public void shouldThrowAnErrorBecauseDocumentCpfIsInvalid() throws Exception {
        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "DOCUMENT_CPF",
                                        "valueKey": "1671617678",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira",
                                        "personType": "JURIDICAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("CPF inválido"));

        assertTrue(pixKeyRepository.findAll().size() == 0);
        assertTrue(accountRepository.findAll().size() == 0);
    }

    @DisplayName("Verifica se regra de validação de CPF está correta - cenário ok")
    @Test
    public void shouldRunSuccessBecauseDocumentCpfIsValid() throws Exception {
        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "DOCUMENT_CPF",
                                        "valueKey": "06218585018",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira",
                                        "personType": "JURIDICAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        Optional<Account> optionalAccount = accountRepository.findByAccountTypeAndBranchNumberAndAccountNumber(AccountType.CHECKING_ACCOUNT, 2, 58964);
        assertTrue(optionalAccount.isPresent());

        Account account = optionalAccount.get();

        assertEquals("João", account.getAccountHolderName());
        assertEquals("Silveira", account.getAccountHolderSurname());
        assertEquals(PersonType.JURIDICAL_PERSON, account.getPersonType());
        assertEquals(1, account.getKeys().size());

        Optional<PixKey> optionalPixKey = pixKeyRepository.findByValue("06218585018");
        assertTrue(optionalPixKey.isPresent());

        PixKey pixKey = optionalPixKey.get();
        assertEquals(KeyType.DOCUMENT_CPF, pixKey.getType());
        assertEquals("06218585018", pixKey.getValue());
        assertNotNull(pixKey.getCreationDate());
        assertNotNull(pixKey.getCreationTime());
        assertTrue(pixKey.isActive());
    }

    @DisplayName("Verifica se regra de validação de CNPJ está correta - cenário de erro")
    @Test
    public void shouldThrowAnErrorBecauseDocumentCnpjIsInvalid() throws Exception {
        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "DOCUMENT_CNPJ",
                                        "valueKey": "1671617678",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira",
                                        "personType": "JURIDICAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("CNPJ inválido"));

        assertTrue(pixKeyRepository.findAll().size() == 0);
        assertTrue(accountRepository.findAll().size() == 0);
    }

    @DisplayName("Verifica se regra de validação de CNPJ está correta - cenário ok")
    @Test
    public void shouldRunSuccessBecauseDocumentCnpjIsValid() throws Exception {
        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "DOCUMENT_CNPJ",
                                        "valueKey": "08632030000122",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira",
                                        "personType": "JURIDICAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        Optional<Account> optionalAccount = accountRepository.findByAccountTypeAndBranchNumberAndAccountNumber(AccountType.CHECKING_ACCOUNT, 2, 58964);
        assertTrue(optionalAccount.isPresent());

        Account account = optionalAccount.get();

        assertEquals("João", account.getAccountHolderName());
        assertEquals("Silveira", account.getAccountHolderSurname());
        assertEquals(PersonType.JURIDICAL_PERSON, account.getPersonType());
        assertEquals(1, account.getKeys().size());

        Optional<PixKey> optionalPixKey = pixKeyRepository.findByValue("08632030000122");
        assertTrue(optionalPixKey.isPresent());

        PixKey pixKey = optionalPixKey.get();
        assertEquals(KeyType.DOCUMENT_CNPJ, pixKey.getType());
        assertEquals("08632030000122", pixKey.getValue());
        assertNotNull(pixKey.getCreationDate());
        assertNotNull(pixKey.getCreationTime());
        assertTrue(pixKey.isActive());
    }

    @DisplayName("Verifica se regra de validação de celular está correta - cenário de erro sinal '+' ")
    @Test
    public void shouldThrowAnErrorBecauseCellPhoneIsInvalidStartsPlus() throws Exception {
        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "CELLPHONE",
                                        "valueKey": "557908187389",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira",
                                        "personType": "JURIDICAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Número de celular inválido"));

        assertTrue(pixKeyRepository.findAll().size() == 0);
        assertTrue(accountRepository.findAll().size() == 0);
    }

    @DisplayName("Verifica se regra de validação de celular está correta - cenário de erro formato ")
    @Test
    public void shouldThrowAnErrorBecauseCellPhoneIsInvalid() throws Exception {
        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "CELLPHONE",
                                        "valueKey": "+557908187389a",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira",
                                        "personType": "JURIDICAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Número de celular inválido"));

        assertTrue(pixKeyRepository.findAll().size() == 0);
        assertTrue(accountRepository.findAll().size() == 0);
    }

    @DisplayName("Verifica se regra de validação de celular está correta - cenário de erro formato 2")
    @Test
    public void shouldThrowAnErrorBecauseCellPhoneIsInvalidFormat() throws Exception {
        mockMvc.perform(post("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    {
                                        "type": "CELLPHONE",
                                        "valueKey": "+aaaaaa",
                                        "accountType": "CHECKING_ACCOUNT",
                                        "branchNumber": 2,
                                        "accountNumber": 58964,
                                        "accountHolderName": "João",
                                        "accountHolderSurname": "Silveira",
                                        "personType": "JURIDICAL_PERSON"
                                    }
                                """)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Número de celular inválido"));

        assertTrue(pixKeyRepository.findAll().size() == 0);
        assertTrue(accountRepository.findAll().size() == 0);
    }
}
