package Exceptions.User;

public class InvalidUsernameException extends Exception{
    public InvalidUsernameException() {
        super();
    }

    public InvalidUsernameException(String s) {
        super("Lo username " + s + " contiene caratteri non validi");
    }
}
