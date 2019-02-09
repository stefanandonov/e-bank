package mk.ukim.finki.css.ebankapi.service;

import mk.ukim.finki.css.ebankapi.model.DTO.TransactionDTO;
import mk.ukim.finki.css.ebankapi.model.Transaction;
import mk.ukim.finki.css.ebankapi.model.User;
import mk.ukim.finki.css.ebankapi.model.exceptions.*;

import java.util.*;

public interface UsersService {

    String addClient(String username, String name,
                   String telephone, Double startBalance,
                   String accountNumber, String address, Long employeeId,
                   StringBuilder sb)
            throws UsernameAlreadyExistException,
            AccountNumberAlreadyExistsException,
            AccountNumberNotValidException,
            UserDoesNotExistException,
            NotSufficientPermissionExpcetion,
            UserNotLoggedInException;

    User login (String username,
                   String password)
            throws UserDoesNotExistException,
            WrongPasswordException;

    Long getUserId (String user) throws UserDoesNotExistException;

    Long getUserIdByAccountNumber (String account) throws UserDoesNotExistException;

    void logout (Long id)
            throws UserNotLoggedInException;
    User changeClientPassword (String username,
                               String newPassword)
            throws UserDoesNotExistException,
            UserNotLoggedInException;

    Transaction makeTransactions (Long senderId,
                                  Long recieverId,
                                  Long employeeId,
                                  Double amount,
                                  Integer tokenItemNumber,
                                  Long tokenNumber,
                                  String senderPassword)
            throws UserDoesNotExistException,
            NotSufficientPermissionExpcetion,
            UserNotLoggedInException;

    String createTokensPerClient (Long clientId,
                                  StringBuilder sb)
            throws UserDoesNotExistException;

    Integer chooseRandomTokenForVerification();

    void twoFactorAuthentication (Long id,
                                  Integer itemNumber,
                                  Long tokenNumber,
                                  String senderPassword)
            throws UserDoesNotExistException,
            NotSufficientPermissionExpcetion,
            UserNotLoggedInException;

    List<TransactionDTO> getTransactionsByUser (Long id)
            throws UserDoesNotExistException,
            UserNotLoggedInException;

    List<User> getClients();

}
