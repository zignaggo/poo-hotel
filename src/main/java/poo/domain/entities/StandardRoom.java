package poo.domain.entities;

public class StandardRoom extends Room {
    public StandardRoom(int id, int number, int capacity, double pricePerNight) {
        super(id, number, capacity, pricePerNight, "STANDARD");
    }
    
    @Override
    public double calculatePrice(int nights) {
        return super.calculatePrice(nights);
    }
}
