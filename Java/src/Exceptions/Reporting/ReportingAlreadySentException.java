package Exceptions.Reporting;

public class ReportingAlreadySentException extends Exception {
    public ReportingAlreadySentException() {
        super();
    }

    public ReportingAlreadySentException(String s) {
        super(s);
    }
}
