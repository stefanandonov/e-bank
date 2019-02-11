package mk.ukim.finki.css.ebankapi.service.impl;

import mk.ukim.finki.css.ebankapi.model.*;
import mk.ukim.finki.css.ebankapi.model.DTO.TransactionDTO;
import mk.ukim.finki.css.ebankapi.model.enums.Role;
import mk.ukim.finki.css.ebankapi.model.exceptions.*;
import mk.ukim.finki.css.ebankapi.repository.*;
import mk.ukim.finki.css.ebankapi.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
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
    private final Random random = new Random();

    private List<Long> loggedInUsers = new ArrayList<>();

    @Autowired
    public UsersServiceImpl(TokensRepository tokensRepository,
                            TransactionsRepository transactionsRepository,
                            UsersRepository usersRepository,
                            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.tokensRepository = tokensRepository;
        this.transactionsRepository = transactionsRepository;
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;

    }

    private static boolean checkValidityForAccountNumber (String accountNumber) {
        if (accountNumber.length()!=15)
            return false;
        for (int i=0;i<accountNumber.length();i++)
            if (!Character.isDigit(accountNumber.charAt(i)))
                return false;

        return true;
    }

    private String generateRandomPassword ()  {
        RandomValueStringGenerator randomValueStringGenerator = new RandomValueStringGenerator(10);
        return randomValueStringGenerator.generate();
    }

    @Override
    public String addClient(String username, String name,
                          String telephone, Double startBalance,
                          String accountNumber, String address, Long employeeId,
                          StringBuilder sb)
            throws UsernameAlreadyExistException, AccountNumberAlreadyExistsException, AccountNumberNotValidException, UserDoesNotExistException, NotSufficientPermissionExpcetion, UserNotLoggedInException {
        /*
        * Function that creates a client whenever a bank employee creates one. Recieves as arguments all the nessecary informations
        * for the client (username, password (plain text), name, telephone number, start balance, account number, home addres.
        * - checks for unique status of username and account number
        * - account number has to be a string with 15 digits.
        * If everything is okay, the method saves the new user in the database.
        * */

        User employee = usersRepository.findById(employeeId).orElseThrow(() -> new NotSufficientPermissionExpcetion());

        if (employee.role!=Role.EMPLOYEE)
            throw new NotSufficientPermissionExpcetion();

        isUserLoggedIn(employeeId);

        User u = usersRepository.findByUsername(username).orElse(null);
        sb.append("Username: ").append(username).append("\n");
        if (u!=null)
            throw new UsernameAlreadyExistException(username);

        u = usersRepository.findByAccoutNumber(accountNumber).orElse(null);

        if (u!= null)
            throw new AccountNumberAlreadyExistsException(accountNumber);

        if (!checkValidityForAccountNumber(accountNumber))
            throw new AccountNumberNotValidException(accountNumber);

        String password = generateRandomPassword();
        sb.append("Password: ").append(password).append("\n");;
        String hashedPassword = bCryptPasswordEncoder.encode(password);

        User newUser = new User(username,hashedPassword,name,telephone,startBalance,accountNumber,address);
        usersRepository.save(newUser);

        u = usersRepository.findByUsername(username).orElse(null);
        String report = createTokensPerClient(u.getId(),sb);
        return report;

    }

    @Override
    public User login(String username, String password) throws UserDoesNotExistException, WrongPasswordException {

        /*User user = new User ();
        user.setRole(Role.EMPLOYEE);
        user.setUsername("stefan5andonov");
        user.setPassword(bCryptPasswordEncoder.encode("admin"));
        usersRepository.save(user);*/

        User client = usersRepository.findByUsername(username).orElseThrow(() -> new UserDoesNotExistException(username));
        boolean correctPassword = bCryptPasswordEncoder.matches(password,client.getPassword());
        if (!correctPassword)
            throw new WrongPasswordException();
        loggedInUsers.add(client.getId());
        return client;
    }

    @Override
    public Long getUserId(String username) throws UserDoesNotExistException {
        return usersRepository.findByUsername(username).orElseThrow(() -> new UserDoesNotExistException(username)).getId();
    }

    @Override
    public Long getUserIdByAccountNumber(String account) throws UserDoesNotExistException {
        return usersRepository.findByAccoutNumber(account).orElseThrow(()->new UserDoesNotExistException(account)).getId();
    }

    @Override
    public void logout(Long id) throws UserNotLoggedInException {
        isUserLoggedIn(id);
        loggedInUsers.remove(id);
    }

    @Override
    public User changeClientPassword(String username, String newPassword) throws UserDoesNotExistException, UserNotLoggedInException {

        User client = usersRepository.findByUsername(username).orElseThrow(() -> new UserDoesNotExistException(username));
        isUserLoggedIn(client.getId());

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
            throws UserDoesNotExistException, NotSufficientPermissionExpcetion, UserNotLoggedInException, TokenDoesNotExistException {

        if (senderId==null || recieverId == null || amount == null)
            throw new IllegalArgumentException();

        User sender = usersRepository.findById(senderId).orElseThrow(() -> new UserDoesNotExistException(""));
        User reciever = usersRepository.findById(recieverId).orElseThrow(() -> new UserDoesNotExistException(""));
        User employee = null;

        if (employeeId==null) { //Online transaction

           isUserLoggedIn(senderId);

            this.twoFactorAuthentication(senderId,tokenItemNumber,tokenNumber, senderPassword);

        }
        else { //Offline transaction
            employee = usersRepository.findById(employeeId).orElse(null);
            if (employee==null)
                throw new UserDoesNotExistException("");

            isUserLoggedIn(employeeId);

            if (employee.getRole()!= Role.EMPLOYEE) {
                throw new NotSufficientPermissionExpcetion();
            }

        }

        sender.setBalance(sender.getBalance()-amount);
        reciever.setBalance(reciever.getBalance()+amount);
        usersRepository.save(sender);
        usersRepository.save(reciever);
        return transactionsRepository.save(new Transaction(sender,reciever,employee,amount,ZonedDateTime.now()));
    }

    private Long randLong() {
        Long aLong =  (Long.valueOf(random.nextInt((999999 - 100000) + 1) + 1000000));
        return aLong;
    }


    private Long generateRandomNumber() {

        return randLong();

    }
    @Override
    public String createTokensPerClient(Long clientId, StringBuilder sb) throws UserDoesNotExistException {
        sb.append("Tokens: \n");
        User client = usersRepository.findById(clientId).orElseThrow(() -> new UserDoesNotExistException(""));
        List<Long> assignedTokens = new ArrayList<>();
        for (int i=1;i<=40;i++) {
            Long tokenNumber = generateRandomNumber();

            if (assignedTokens.isEmpty()){
                assignedTokens.add(tokenNumber);
                sb.append(String.format("%d: %d\n", i, tokenNumber));
            }
            else {
                while(assignedTokens.contains(tokenNumber)) tokenNumber = generateRandomNumber();
                assignedTokens.add(tokenNumber);
                sb.append(String.format("%d: %d\n", i, tokenNumber));
            }
        }

        assignedTokens.stream()
                .map(tokenNumber -> new Token(assignedTokens.indexOf(tokenNumber)+1,tokenNumber,client))
                .forEach(token -> tokensRepository.save(token));

        return sb.toString();
    }

    @Override
    public Integer chooseRandomTokenForVerification(Long clientId) {

        List<Token> tokens = tokensRepository.findAllByUserId(clientId);

        int randomPick = random.nextInt(tokens.size());

        return tokens.get(randomPick).itemNumber;
    }

    @Override
    public void twoFactorAuthentication(Long clientId, Integer itemNumber, Long tokenNumber, String senderPassword) throws UserDoesNotExistException, NotSufficientPermissionExpcetion, UserNotLoggedInException, TokenDoesNotExistException {

        isUserLoggedIn(clientId);

        List<Token> tokensPerClient = tokensRepository.findAllByUserId(clientId);

        if (tokensPerClient==null)
            throw new UserDoesNotExistException("");
        System.out.println(itemNumber.toString() + " "
                + tokenNumber.toString() + " " + (itemNumber-1) + " " + tokensPerClient.get(itemNumber - 1).tokenNumber);
        if (!tokenNumber.equals(tokensPerClient.get(itemNumber - 1).tokenNumber))
            throw new NotSufficientPermissionExpcetion();

        User user = usersRepository.findById(clientId).orElseThrow(() -> new UserDoesNotExistException(""));
        if (!bCryptPasswordEncoder.matches(senderPassword,user.getPassword()))
            throw new NotSufficientPermissionExpcetion();

       /* Token tokenToDelete = tokensRepository.findByUserIdAndItemNumber(clientId,itemNumber-1)
                .orElseThrow(() -> new TokenDoesNotExistException());

        tokensRepository.delete(tokenToDelete);*/


    }

    private TransactionDTO createTransactionDTO (Transaction t, Long clientId) throws UserDoesNotExistException {

        String senderOrReciever;
        Double amount = t.amount;
        ZonedDateTime date = t.date;

        if (t.sender.getId().equals(clientId)) {
            senderOrReciever = usersRepository
                    .findById(t.reciever.getId())
                    .orElse(null)
                    .name;
            amount *= -1;
        }
        else {
            senderOrReciever = usersRepository
                    .findById(t.sender.getId())
                    .orElse(null)
                    .name;

        }

        return new TransactionDTO(date,senderOrReciever,amount);

    }

    @Override
    public List<TransactionDTO> getTransactionsByUser(Long clientId) throws UserDoesNotExistException, UserNotLoggedInException {

        isUserLoggedIn(clientId);

        User user = usersRepository.findById(clientId)
                .orElseThrow(()-> new UserDoesNotExistException(clientId.toString()));

        List<Transaction> transactions = transactionsRepository.findAllByRecieverOrSender(user,user);


        List<TransactionDTO> list = new ArrayList<>();
        for (Transaction transaction : transactions) {
            TransactionDTO transactionDTO = this.createTransactionDTO(transaction, clientId);
            list.add(transactionDTO);
        }
        return list;



    }

    @Override
    public List<User> getClients() {

        return usersRepository.findAll()
                .stream()
                .filter(user -> user.role==Role.CLIENT)
                .collect(Collectors.toList());

    }

    @Override
    public Double balance(String username) throws UserDoesNotExistException {
        return  usersRepository.findByUsername(username)
                .orElseThrow(()->new UserDoesNotExistException(username))
                .balance;
    }

    private void isUserLoggedIn(Long id) throws UserNotLoggedInException {
        if(!this.loggedInUsers.contains(id)) {
            System.out.println(this.loggedInUsers.toString() + " "+ id +" " +
                    usersRepository.findById(id).orElse(null).getUsername());
            throw new UserNotLoggedInException();
        }
    }
}
