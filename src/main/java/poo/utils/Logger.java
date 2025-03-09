package poo.utils;

public class Logger {
  static final boolean DEBUG = Boolean.parseBoolean(System.getenv("DEBUG"));
  public static void println(String message) {
    if (DEBUG) {
      System.out.println(message);
    }
  }
  public static void printf(String message, Object... args) {
    if (DEBUG) {
      System.out.printf(message, args);
    }
  }
  public static void print(String message) {
    if (DEBUG) {
      System.out.print(message);
    }
  }
}
