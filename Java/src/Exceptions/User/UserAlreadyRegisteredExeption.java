package Exceptions.User;

public class UserAlreadyRegisteredExeption extends Exception {
    public UserAlreadyRegisteredExeption() {
        super();
    }

    public UserAlreadyRegisteredExeption(String user) {
        super("L'utente " + user + " è già registrato");
    }
}
