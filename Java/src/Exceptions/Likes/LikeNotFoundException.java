package Exceptions.Likes;

public class LikeNotFoundException extends Exception {
    public LikeNotFoundException() {
        super();
    }

    public LikeNotFoundException(String liker, long postId) {
        super("Impossibile trovare il like da parte di " + liker + " al post " + postId);
    }
}
