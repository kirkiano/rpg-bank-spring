package org.kirkiano.finance.bank.repository;

import java.util.List;

import org.kirkiano.finance.bank.model.CharId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
// import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import org.kirkiano.finance.bank.model.Account;


/**
 * Data store of accounts
 */
// @RepositoryRestResource
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Retrieve an account by its owner's ID
     * @param charId owner's ID
     * @return the account
     */
    @Query(value = "select a from Account a where a.charId = ?1")
    List<Account> findByCharId(CharId charId);
}