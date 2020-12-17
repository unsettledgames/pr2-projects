package Exceptions.Post;

public class PostNotFoundException extends Exception {
    public PostNotFoundException() {
        super();
    }

    public PostNotFoundException(long id) {
        super("Impossibile trovare il post " + id);
    }

    public PostNotFoundException(String s) {
        super(s);
    }
}
