package mk.ukim.finki.css.ebankapi.service;

import mk.ukim.finki.css.ebankapi.model.DTO.TransactionDTO;
import mk.ukim.finki.css.ebankapi.model.Transaction;
import mk.ukim.finki.css.ebankapi.model.User;
import mk.ukim.finki.css.ebankapi.model.exceptions.*;

import java.util.*;

public interface UsersService {

    void addClient (String username, String password, String name,
                    String telephone,  Double startBalance, String accountNumber, String addres) throws UsernameAlreadyExistException, AccountNumberAlreadyExistsException, AccountNumberNotValidException, UserDoesNotExistException;


    boolean canLogin (String username, String password) throws UserDoesNotExistException, WrongPasswordException;

    User changeClientPassword (String username, String newPassword) throws UserDoesNotExistException;

    Transaction makeTransactions (Long senderId, Long recieverId, Long employeeId, Double amount,
                                  Integer tokenItemNumber,
                                  Long tokenNumber,
                                  String senderPassword) throws UserDoesNotExistException, NotSufficientPermissionExpcetion;

    void createTokensPerClient (Long clientId) throws UserDoesNotExistException;

    Integer chooseRandomTokenForVerification();

    void twoFactorAuthentication (Long id, Integer itemNumber, Long tokenNumber, String senderPassword) throws UserDoesNotExistException, NotSufficientPermissionExpcetion;

    List<TransactionDTO> getTransactionsByUser (Long id) throws UserDoesNotExistException;

    List<User> getClients();

}
