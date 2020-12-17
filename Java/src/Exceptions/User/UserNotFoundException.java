package Exceptions.User;

public class UserNotFoundException extends Exception{
    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String user) {
        super("Impossibile trovare l'utente " + user);
    }

    public UserNotFoundException(String message, String user) {
        super(String.format(message, user));
    }
}
