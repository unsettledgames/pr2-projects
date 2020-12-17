package Exceptions.Likes;

public class AutoLikeException extends Exception{
    public AutoLikeException() {
        super();
    }

    public AutoLikeException(String liker, long postId) {
        super("Impossibile mettere like da parte di " + liker + " al post " + postId + " poiché è stato lui a pubblicarlo");
    }
}
