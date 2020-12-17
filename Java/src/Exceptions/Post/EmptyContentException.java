package Exceptions.Post;

public class EmptyContentException extends Exception {
    public EmptyContentException (){
        super();
    }

    public EmptyContentException(String s) {
        super(s);
    }
}
