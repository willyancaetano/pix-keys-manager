package com.pix.keys.integration.usecases;

import com.pix.keys.dto.AccountType;
import com.pix.keys.dto.KeyType;
import com.pix.keys.dto.PersonType;
import com.pix.keys.model.Account;
import com.pix.keys.model.PixKey;
import com.pix.keys.repository.AccountRepository;
import com.pix.keys.repository.PixKeyRepository;
import org.hamcrest.Matchers;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchPixKeysUseCaseTest {

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

    @DisplayName("Valida critério de aceite de consulta combinada de filtros")
    @Test
    public void shouldSearchSuccessfully() throws Exception {

        Account account = new Account(AccountType.CHECKING_ACCOUNT, 1, 12345, "Claudia",
                "Beatriz", PersonType.NATURAL_PERSON);
        accountRepository.save(account);

        PixKey pixKey = new PixKey(KeyType.RANDOM, "1d1d6467-e939-46e0-94e6-9f5298fb5e40", account);
        pixKeyRepository.save(pixKey);

        String today = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now());

        mockMvc.perform(get("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("type", "RANDOM")
                        .header("branch-number", 1)
                        .header("account-number", 12345)
                        .header("holder-name", "Claudia")
                        .header("creation-date", LocalDate.now().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(pixKey.getId()))
                .andExpect(jsonPath("$[0].type").value("RANDOM"))
                .andExpect(jsonPath("$[0].valueKey").value("1d1d6467-e939-46e0-94e6-9f5298fb5e40"))
                .andExpect(jsonPath("$[0].accountType").value("CHECKING_ACCOUNT"))
                .andExpect(jsonPath("$[0].branchNumber").value("1"))
                .andExpect(jsonPath("$[0].accountNumber").value("12345"))
                .andExpect(jsonPath("$[0].accountHolderName").value("Claudia"))
                .andExpect(jsonPath("$[0].accountHolderSurname").value("Beatriz"))
                .andExpect(jsonPath("$[0].creationDate").value(today))
                .andExpect(jsonPath("$[0].inactivationDate").value(""));
    }

    @DisplayName("Valida se busca apenas por id está ok")
    @Test
    public void shouldSearchComIdComSuccess() throws Exception {

        Account account = new Account(AccountType.CHECKING_ACCOUNT, 1, 12345, "Claudia",
                "Beatriz", PersonType.NATURAL_PERSON);
        accountRepository.save(account);

        PixKey pixKey = new PixKey(KeyType.RANDOM, "1d1d6467-e939-46e0-94e6-9f5298fb5e40", account);
        pixKeyRepository.save(pixKey);

        String today = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now());

        mockMvc.perform(get("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("id", pixKey.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(pixKey.getId()))
                .andExpect(jsonPath("$[0].type").value("RANDOM"))
                .andExpect(jsonPath("$[0].valueKey").value("1d1d6467-e939-46e0-94e6-9f5298fb5e40"))
                .andExpect(jsonPath("$[0].accountType").value("CHECKING_ACCOUNT"))
                .andExpect(jsonPath("$[0].branchNumber").value("1"))
                .andExpect(jsonPath("$[0].accountNumber").value("12345"))
                .andExpect(jsonPath("$[0].accountHolderName").value("Claudia"))
                .andExpect(jsonPath("$[0].accountHolderSurname").value("Beatriz"))
                .andExpect(jsonPath("$[0].creationDate").value(today))
                .andExpect(jsonPath("$[0].inactivationDate").value(""));
    }

    @DisplayName("Valida se busca dois registros por data de inativação")
    @Test
    public void shouldSearchByInactivationDate() throws Exception {

        Account account = new Account(AccountType.CHECKING_ACCOUNT, 1, 12345, "Claudia",
                "Beatriz", PersonType.NATURAL_PERSON);
        accountRepository.save(account);

        PixKey pixKey = new PixKey(KeyType.RANDOM, "1d1d6467-e939-46e0-94e6-9f5298fb5e40", account);
        pixKey.inactivateKey();
        pixKeyRepository.save(pixKey);

        pixKey = new PixKey(KeyType.EMAIL, "will@gmail.com", account);
        pixKey.inactivateKey();
        pixKeyRepository.save(pixKey);

        String today = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now());

        mockMvc.perform(get("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("inactivation-date", LocalDate.now().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[1].id").value(pixKey.getId()))
                .andExpect(jsonPath("$[1].type").value("EMAIL"))
                .andExpect(jsonPath("$[1].valueKey").value("will@gmail.com"))
                .andExpect(jsonPath("$[1].accountType").value("CHECKING_ACCOUNT"))
                .andExpect(jsonPath("$[1].branchNumber").value("1"))
                .andExpect(jsonPath("$[1].accountNumber").value("12345"))
                .andExpect(jsonPath("$[1].accountHolderName").value("Claudia"))
                .andExpect(jsonPath("$[1].accountHolderSurname").value("Beatriz"))
                .andExpect(jsonPath("$[1].creationDate").value(today))
                .andExpect(jsonPath("$[1].inactivationDate").value(today));
    }

    @DisplayName("Valida se busca dois registros")
    @Test
    public void shouldFetchTwoRecords() throws Exception {

        Account account = new Account(AccountType.CHECKING_ACCOUNT, 1, 12345, "Claudia",
                "Beatriz", PersonType.NATURAL_PERSON);
        accountRepository.save(account);

        PixKey pixKey = new PixKey(KeyType.RANDOM, "1d1d6467-e939-46e0-94e6-9f5298fb5e40", account);
        pixKeyRepository.save(pixKey);

        pixKey = new PixKey(KeyType.EMAIL, "will@gmail.com", account);
        pixKeyRepository.save(pixKey);

        String today = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now());

        mockMvc.perform(get("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("branch-number", 1)
                        .header("account-number", 12345)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[1].id").value(pixKey.getId()))
                .andExpect(jsonPath("$[1].type").value("EMAIL"))
                .andExpect(jsonPath("$[1].valueKey").value("will@gmail.com"))
                .andExpect(jsonPath("$[1].accountType").value("CHECKING_ACCOUNT"))
                .andExpect(jsonPath("$[1].branchNumber").value("1"))
                .andExpect(jsonPath("$[1].accountNumber").value("12345"))
                .andExpect(jsonPath("$[1].accountHolderName").value("Claudia"))
                .andExpect(jsonPath("$[1].accountHolderSurname").value("Beatriz"))
                .andExpect(jsonPath("$[1].creationDate").value(today))
                .andExpect(jsonPath("$[1].inactivationDate").value(""));
    }

    @DisplayName("Valida critério de aceita que informa que nenhum outro filtro pode ser aceito quando id é usado")
    @Test
    public void shouldValidateIfOnlyIdWasInformed() throws Exception {

        Account account = new Account(AccountType.CHECKING_ACCOUNT, 1, 12345, "Claudia",
                "Beatriz", PersonType.NATURAL_PERSON);
        accountRepository.save(account);

        PixKey pixKey = new PixKey(KeyType.RANDOM, "1d1d6467-e939-46e0-94e6-9f5298fb5e40", account);
        pixKeyRepository.save(pixKey);

        String today = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now());

        mockMvc.perform(get("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("id", pixKey.getId())
                        .header("branch-number", 1)
                        .header("account-number", 12345)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Quando a consulta possui ID, não é aceito nenhum outro parâmetro"));
    }

    @DisplayName("Validação de critério de aceita onde não é permitido a combinação das datas")
    @Test
    public void shouldValidateThatOnlyOneDateWasSent() throws Exception {

        Account account = new Account(AccountType.CHECKING_ACCOUNT, 1, 12345, "Claudia",
                "Beatriz", PersonType.NATURAL_PERSON);
        accountRepository.save(account);

        PixKey pixKey = new PixKey(KeyType.RANDOM, "1d1d6467-e939-46e0-94e6-9f5298fb5e40", account);
        pixKeyRepository.save(pixKey);

        String today = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now());

        mockMvc.perform(get("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("branch-number", 1)
                        .header("account-number", 12345)
                        .header("creation-date", "2022-05-30")
                        .header("inactivation-date", "2022-05-28")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Filtros data de inclusão da chave e data da inativação da chave não" +
                        "são permitidos juntos"));
    }

    @DisplayName("Valida critério de aceite quando retorno deve ser 404 por não existir chave")
    @Test
    public void shouldThrowExceptionBecauseDataDoesNotExist() throws Exception {

        Account account = new Account(AccountType.CHECKING_ACCOUNT, 1, 12345, "Claudia",
                "Beatriz", PersonType.NATURAL_PERSON);
        accountRepository.save(account);

        PixKey pixKey = new PixKey(KeyType.RANDOM, "1d1d6467-e939-46e0-94e6-9f5298fb5e40", account);
        pixKeyRepository.save(pixKey);

        String today = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now());

        mockMvc.perform(get("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("branch-number", 2)
                        .header("account-number", 12345)
                        .header("creation-date", "2022-05-30")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Não foram encontrados dados para essa consulta"));
    }

    @DisplayName("Valida cenário de erro na busca de id")
    @Test
    public void shouldSearchComIdWithError() throws Exception {

        Account account = new Account(AccountType.CHECKING_ACCOUNT, 1, 12345, "Claudia",
                "Beatriz", PersonType.NATURAL_PERSON);
        accountRepository.save(account);

        PixKey pixKey = new PixKey(KeyType.RANDOM, "1d1d6467-e939-46e0-94e6-9f5298fb5e40", account);
        pixKeyRepository.save(pixKey);

        mockMvc.perform(get("/v1/pix-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("id", UUID.randomUUID().toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Id da chave não existe"));
    }
}
