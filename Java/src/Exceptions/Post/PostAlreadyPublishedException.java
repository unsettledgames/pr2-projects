package Exceptions.Post;

public class PostAlreadyPublishedException extends Exception{
    public PostAlreadyPublishedException() {
        super();
    }

    public PostAlreadyPublishedException(long id) {
        super("Il post con id " + id + " è già presente all'interno della rete.");
    }
}
