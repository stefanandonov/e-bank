package mk.ukim.finki.css.ebankapi.service.impl;

import mk.ukim.finki.css.ebankapi.model.*;
import mk.ukim.finki.css.ebankapi.model.DTO.TransactionDTO;
import mk.ukim.finki.css.ebankapi.model.enums.Role;
import mk.ukim.finki.css.ebankapi.model.exceptions.*;
import mk.ukim.finki.css.ebankapi.repository.*;
import mk.ukim.finki.css.ebankapi.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UsersServiceImpl implements UsersService {

    private TokensRepository tokensRepository;
    private TransactionsRepository transactionsRepository;
    private UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Random random;

    @Autowired
    public UsersServiceImpl(TokensRepository tokensRepository,
                            TransactionsRepository transactionsRepository,
                            UsersRepository usersRepository,
                            BCryptPasswordEncoder bCryptPasswordEncoder,
                            Random random) {
        this.tokensRepository = tokensRepository;
        this.transactionsRepository = transactionsRepository;
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.random=random;
    }

    private static boolean checkValidityForAccountNumber (String accountNumber) {
        if (accountNumber.length()!=15)
            return false;
        for (int i=0;i<accountNumber.length();i++)
            if (!Character.isDigit(accountNumber.charAt(i)))
                return false;

        return true;
    }
    @Override
    public void addClient(String username, String password, String name,
                          String telephone, Double startBalance,
                          String accountNumber, String address)
            throws UsernameAlreadyExistException, AccountNumberAlreadyExistsException, AccountNumberNotValidException {
        /*
        * Function that creates a client whenever a bank employee creates one. Recieves as arguments all the nessecary informations
        * for the client (username, password (plain text), name, telephone number, start balance, account number, home addres.
        * - checks for unique status of username and account number
        * - account number has to be a string with 15 digits.
        * If everything is okay, the method saves the new user in the database.
        * */

        User u = usersRepository.findByUsername(username).orElse(null);

        if (u!=null)
            throw new UsernameAlreadyExistException(username);

        u = usersRepository.findByAccoutNumber(accountNumber).orElse(null);

        if (u!= null)
            throw new AccountNumberAlreadyExistsException(accountNumber);

        if (!checkValidityForAccountNumber(accountNumber))
            throw new AccountNumberNotValidException(accountNumber);

        //String hashedPassword = BCryptPasswordEncoder.hashpw(password,SALT);
        String hashedPassword = bCryptPasswordEncoder.encode(password);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setName(name);
        newUser.setPassword(hashedPassword);
        newUser.setAccoutNumber(accountNumber);
        newUser.setTelephone(telephone);
        newUser.setAddress(address);
        newUser.setStartBalance(startBalance);

        usersRepository.save(newUser);

        u = usersRepository.findByUsername(username).orElse(null);
        createTokensPerClient(u.getId());

    }

    @Override
    public boolean canLogin(String username, String password) throws UserDoesNotExistException, WrongPasswordException {

        User client = usersRepository.findByUsername(username).orElseThrow(() -> new UserDoesNotExistException(username));

        //boolean correctPassword = BCrypt.checkpw(password,client.getPassword());
        boolean correctPassword = bCryptPasswordEncoder.matches(password,client.getPassword());


        if (!correctPassword)
            throw new WrongPasswordException();

        return true;
    }

    @Override
    public User changeClientPassword(String username, String newPassword) throws UserDoesNotExistException {

        User client = usersRepository.findByUsername(username).orElseThrow(() -> new UserDoesNotExistException(username));

        //String hashedNewPassword = BCrypt.hashpw(newPassword,SALT);
        String hashedNewPassword = bCryptPasswordEncoder.encode(newPassword);

        client.setPassword(hashedNewPassword);

        return usersRepository.save(client);
    }

    @Override
    public Transaction makeTransactions(Long senderId,
                                        Long recieverId,
                                        Long employeeId,
                                        Double amount,
                                        Integer tokenItemNumber,
                                        Long tokenNumber,
                                        String senderPassword)
            throws UserDoesNotExistException, NotSufficientPermissionExpcetion {

        if (senderId==null || recieverId == null || amount == null)
            throw new IllegalArgumentException();

        User sender = usersRepository.findById(senderId).orElseThrow(() -> new UserDoesNotExistException(""));
        User reciever = usersRepository.findById(recieverId).orElseThrow(() -> new UserDoesNotExistException(""));

        if (employeeId==null) { //Online transaction
            //method for two-factor authentication of the sender!!
            this.twoFactorAuthentication(senderId,tokenItemNumber,tokenNumber, senderPassword);

        }
        else { //Offline transaction

            User employee = usersRepository.findById(employeeId).orElseThrow(() -> new UserDoesNotExistException(""));

            if (employee.getRole()!= Role.EMPLOYEE)
                throw new NotSufficientPermissionExpcetion();

        }

        sender.setBalance(sender.getBalance()-amount);
        reciever.setBalance(reciever.getBalance()+amount);
        usersRepository.save(sender);
        usersRepository.save(reciever);
        return transactionsRepository.save(new Transaction(senderId,recieverId,null,amount,ZonedDateTime.now()));
    }

    private Long randLong(Long min, Long max) {
        return (random.nextLong() % (max - min)) + min;
    }


    private Long generateRandomNumber() {

        return randLong(10000000l,99999999l);

    }
    @Override
    public void createTokensPerClient(Long clientId) {

        List<Long> assignedTokens = new ArrayList<>();
        for (int i=1;i<=40;i++) {
            Long tokenNumber = generateRandomNumber();

            if (assignedTokens.isEmpty())
                assignedTokens.add(tokenNumber);
            else {
                while(assignedTokens.contains(tokenNumber)) tokenNumber = generateRandomNumber();
                assignedTokens.add(tokenNumber);
            }
        }

        assignedTokens.stream()
                .map(tokenNumber -> new Token(assignedTokens.indexOf(tokenNumber)+1,tokenNumber,clientId))
                .forEach(token -> tokensRepository.save(token));

    }

    @Override
    public Integer chooseRandomTokenForVerification() {
        return random.nextInt(40)+1;
    }

    @Override
    public void twoFactorAuthentication(Long clientId, Integer itemNumber, Long tokenNumber, String senderPassword) throws UserDoesNotExistException, NotSufficientPermissionExpcetion {
        List<Token> tokensPerClient = tokensRepository.findAllByUser(clientId);

        if (tokensPerClient==null)
            throw new UserDoesNotExistException("");

        if (!tokenNumber.equals(tokensPerClient.get(itemNumber + 1).tokenNumber))
            throw new NotSufficientPermissionExpcetion();

        User user = usersRepository.findById(clientId).orElseThrow(() -> new UserDoesNotExistException(""));
        if (!bCryptPasswordEncoder.matches(senderPassword,user.getPassword()))
            throw new NotSufficientPermissionExpcetion();


    }

    private TransactionDTO createTransactionDTO (Transaction t, Long clientId) throws UserDoesNotExistException {

        String senderOrReciever;
        Double amount = t.amount;
        ZonedDateTime date = t.date;

        if (t.sender.equals(clientId)) {
            senderOrReciever = usersRepository
                    .findById(t.reciever)
                    .orElse(null)
                    .name;
            amount *= -1;
        }
        else {
            senderOrReciever = usersRepository
                    .findById(t.sender)
                    .orElse(null)
                    .name;

        }

        return new TransactionDTO(date,senderOrReciever,amount);

    }

    @Override
    public List<TransactionDTO> getTransactionsByUser(Long clientId) throws UserDoesNotExistException {


        List<Transaction> transactions = transactionsRepository.findAllByRecieverOrSender(clientId,clientId);


        List<TransactionDTO> list = new ArrayList<>();
        for (Transaction transaction : transactions) {
            TransactionDTO transactionDTO = this.createTransactionDTO(transaction, clientId);
            list.add(transactionDTO);
        }
        return list;



    }
}
