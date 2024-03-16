package org.kirkiano.finance.bank.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import org.kirkiano.finance.bank.exn.UnknownCharIdException;
import org.kirkiano.finance.bank.exn.AccountAlreadyExistsException;
import org.kirkiano.finance.bank.exn.NegativeBalanceException;
import org.kirkiano.finance.bank.exn.NoSuchAccountIdException;
import org.kirkiano.finance.bank.model.Account;
import org.kirkiano.finance.bank.model.CharId;
import org.kirkiano.finance.bank.model.Money;
import org.kirkiano.finance.bank.repository.AccountRepository;


/**
 * Account service
 */
@Slf4j
@Service
public class AccountService {

    /**
     * Constructor
     *
     * @param accountRepo The accounts repo (an injected dependency).
     */
    @Autowired
    public AccountService(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    /**
     * Retrieve all accounts
     * @return the accounts
     */
    public List<Account> getAllAccounts() {
        return this.accountRepo.findAll();
    }


    /**
     * Create a bank account
     * <p>
     * {@code balance} is nullable rather than {@code Optional}.
     * (It is an anti-pattern to use the latter as a method parameter.
     * See <a href="https://stackoverflow.com/a/26328555">here</a>
     * and
     * <a href="https://www.baeldung.com/java-optional#misuages">here</a>.)
     *
     * @param charId owner's ID
     * @param balance Initial balance. If null, the balance will be zero.
     * @return Account
     * @throws AccountAlreadyExistsException Guards against multiplying accounts
     * @throws NegativeBalanceException Guards against overdraft
     */
    public Account createAccount(CharId charId, @Nullable Money balance)
        throws AccountAlreadyExistsException,
        NegativeBalanceException
    {
        try {
            balance = balance == null ? Money.ZERO : balance;
            Account account = Account.create(charId, balance);
            this.accountRepo.save(account);
            log.info("Created {}", account);
            return account;
        }
        catch (DataIntegrityViolationException _ex) {
            log.warn("Attempted to create duplicate account for {}", charId);
            throw new AccountAlreadyExistsException(charId);
        }
        catch (NegativeBalanceException ex) {
            log.warn("Attempted to create account with balance {} for {}",
                balance, charId);
            throw ex;
        }
    }


    /**
     * Retrieve a page-worth of accounts
     *
     * @param pageNumber page number
     * @param pageLength number of accounts per page
     * @param sortBy field on which to sort
     * @param isDescending sort order
     * @return the accounts
     */
    public List<Account> getAccountsPage(int pageNumber,
                                         int pageLength,
                                         String sortBy,
                                         boolean isDescending)
    {
        Sort sort = Sort.by(sortBy);
        sort = isDescending ? sort.descending() : sort.ascending();

        Pageable paging = PageRequest.of(pageNumber, pageLength, sort);
        Page<Account> page = this.accountRepo.findAll(paging);
        return page.hasContent() ? page.getContent() : new ArrayList<>();
    }

    /**
     * Fetch an account by ID
     *
     * @param id account ID
     * @return Account
     * @throws NoSuchAccountIdException when {@code id} is invalid
     */
    public Account getAccountById(long id) throws NoSuchAccountIdException {
        Optional<Account> account = this.accountRepo.findById(id);
        if (account.isPresent()) { return account.get(); }
        else { throw new NoSuchAccountIdException(id); }
    }

    /**
     * Fetch an account by owner's ID
     *
     * @param charId character ID
     * @return Account
     * @throws UnknownCharIdException when {@code charId} is not found
     */
    public Account getAccountByCharId(CharId charId)
        throws UnknownCharIdException
    {
        List<Account> accounts = this.accountRepo.findByCharId(charId);
        if (accounts.size() == 0) throw new UnknownCharIdException(charId);
        else return accounts.get(0);
    }

    ///////////////////////////////////////////////////////
    // change balance

    /**
     * Change the balance of an account
     *
     * @param id Account ID
     * @param delta Desired change. A negative quantity means withdrawal.
     * @return New balance
     * @throws NoSuchAccountIdException In case the account does not exist
     * @throws NegativeBalanceException Guards against overdraft
     */
    public Money changeBalance(long id, Money delta)
        throws NoSuchAccountIdException,
               NegativeBalanceException
    {
        Account account = this.accountRepo
            .findById(id)
            .orElseThrow(() -> new NoSuchAccountIdException(id))
            .changeBalance(delta);
        this.accountRepo.save(account);
        return account.getBalance();
    }

    ///////////////////////////////////////////////////////
    // private

    private final AccountRepository accountRepo;
}