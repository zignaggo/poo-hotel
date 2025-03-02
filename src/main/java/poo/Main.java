package poo;

import poo.infra.ConnectionFactory;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = ConnectionFactory.getConnection()) {
            System.out.println("Connected!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}