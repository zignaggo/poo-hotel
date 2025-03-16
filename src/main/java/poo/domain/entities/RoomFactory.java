package poo.domain.entities;

public class RoomFactory {
    public static Room create(String type, int id, int number, int capacity, double pricePerNight) {
        if (type.equals("STANDARD")) {
            return new StandardRoom(id, number, capacity, pricePerNight);
        }
        return new DeluxeRoom(id, number, capacity, pricePerNight, false);
    }

    public static Room create(String type, int id, int number, int capacity, double pricePerNight, boolean hasJacuzzi) {
        if (type.equals("STANDARD")) {
            return new StandardRoom(id, number, capacity, pricePerNight);
        }
        return new DeluxeRoom(id, number, capacity, pricePerNight, hasJacuzzi);
    }

}
