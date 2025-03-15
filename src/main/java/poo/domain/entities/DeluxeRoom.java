package poo.domain.entities;

public class DeluxeRoom extends Room {
    private boolean hasJacuzzi;

    public DeluxeRoom(int id, int number, int capacity, double pricePerNight, boolean hasJacuzzi) {
        super(id, number, capacity, pricePerNight, "DELUXE");
        this.hasJacuzzi = hasJacuzzi;
    }

    public boolean hasJacuzzi() {
        return hasJacuzzi;
    }

    public void setHasJacuzzi(boolean hasJacuzzi) {
        this.hasJacuzzi = hasJacuzzi;
    }


    @Override
    public double calculatePrice(int nights) {
        double acrescentPercent = 1.0;
        double basePrice = super.calculatePrice(nights);
        if (this.hasJacuzzi) {
            acrescentPercent += 0.2;
        }
        return basePrice * acrescentPercent;
    }

}
