package org.academiadecodigo.javabank.services;

import org.academiadecodigo.javabank.model.account.Account;

/**
 * Common interface for account services, provides methods to manage accounts and perform account transactions
 */
public interface AccountService extends CRUDService<Account> {

    /**
     * Perform an {@link Account} deposit
     *
     * @param id     the id of the account
     * @param amount the amount to deposit
     */
    void deposit(Integer id, double amount);

    /**
     * Perform an {@link Account} withdrawal
     *
     * @param id     the id of the account
     * @param amount the amount to withdraw
     */
    void withdraw(Integer id, double amount);

    /**
     * Performs a transfer between two {@link Account} if possible
     *
     * @param srcId  the source account id
     * @param dstId  the destination account id
     * @param amount the amount to transfer
     */
    void transfer(Integer srcId, Integer dstId, double amount);

}
