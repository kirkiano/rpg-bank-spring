package org.kirkiano.finance.bank.controller;

import java.util.function.Supplier;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import org.kirkiano.finance.bank.exn.AccountAlreadyExistsException;
import org.kirkiano.finance.bank.exn.NegativeBalanceException;
import org.kirkiano.finance.bank.model.Account;
import org.kirkiano.finance.bank.model.CharId;
import org.kirkiano.finance.bank.model.Money;
import org.kirkiano.finance.bank.service.AccountService;


/**
 * Abstract base class of tests, providing conveniences
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = org.kirkiano.finance.bank.BankApplication.class
)
@TestPropertySource(
    locations = "classpath:application-integrationtest.properties"
)
public abstract class BaseTest {

    /**
     * Default constructor
     */
    protected BaseTest() {}


    /**
     * Supplier of unique {@link CharId}s, helps integration
     * tests respect db uniqueness constraints.
     */
    protected final Supplier<CharId> charIdGen = new Supplier<>() {
        @Override
        public CharId get() { return this.id.increment(); }
        private static final CharId id = new CharId(0L);
    };


    /**
     * Mock a bank account
     *
     * @param charId Owner ID
     * @param balance Desired initial balance
     * @return The account
     * @throws AccountAlreadyExistsException If the account already exists
     * @throws NegativeBalanceException Guards against negative initial balance
     */
    protected Account mockAccount(CharId charId, Money balance)
        throws AccountAlreadyExistsException,
               NegativeBalanceException
    {
        return accountService.createAccount(charId, balance);
    }


    /**
     * Account service
     */
    @Autowired
    protected AccountService accountService;
}
