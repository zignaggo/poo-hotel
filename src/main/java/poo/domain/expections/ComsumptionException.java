package poo.domain.expections;

public class ComsumptionException extends Exception {
    public ComsumptionException(String message) {
        super(message);
    }
    public ComsumptionException(String message, Exception e) {
        super(message, e);
    }
}
