package poo.domain.expections;

public class ConsumptionException extends Exception {
    public ConsumptionException(String message) {
        super(message);
    }
    public ConsumptionException(String message, Exception e) {
        super(message, e);
    }
}
