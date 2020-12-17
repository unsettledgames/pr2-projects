package Exceptions.Post;

public class PostLengthExceededException extends Exception{
    public PostLengthExceededException() {
        super();
    }

    public PostLengthExceededException(int length) {
        super("Impossibile pubblicare un post di lunghezza " + length);
    }
}
