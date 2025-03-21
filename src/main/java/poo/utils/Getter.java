package poo.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Getter implements AutoCloseable {
  Scanner scanner;

  public Getter() {
    this.scanner = new Scanner(System.in);
    this.scanner.useDelimiter("\n");
  }

  public String readFile(File file) throws FileNotFoundException {
    Scanner scanner = new Scanner(file);
    String result = "";
    while (scanner.hasNextLine()) {
      result += scanner.nextLine() + "\n";
    }
    scanner.close();
    return result;
  }

  public void close() {
    this.scanner.close();
  }

  public int getInt(String message) {
    int value = 0;
    try {
      System.out.println("\n" + message);
      value = Integer.parseInt(this.scanner.next().trim());
      this.scanner.nextLine();
    } catch (Exception e) {
      System.out.println("Write a valid number\n");
      return this.getInt(message);
    }
    return value;
  }

  public double getDouble(String message) {
    double value = 0.0;
    try {
      System.out.println("\n" + message);
      value = Double.parseDouble(this.scanner.next().trim());
      this.scanner.nextLine();
    } catch (Exception e) {
      System.out.println("Write a valid number\n");
      return this.getDouble(message);
    }
    return value;
  }

  public String getString(String message) {
    System.out.println(message);
    this.scanner.skip("\\R?");
    String value = this.scanner.nextLine().trim();
    return value;
  }

  public String getString(String message, String regex) {
    Pattern pattern = Pattern.compile(regex);
    System.out.println(message);
    // https://stackoverflow.com/questions/7946664/scanner-only-reads-first-word-instead-of-line
    this.scanner.skip("\\R?");
    String value = this.scanner.nextLine().trim();
    if (!pattern.matcher(value).matches()) {
      System.out.println("String doesn't match the pattern\n");
      return this.getString(message, regex);
    }
    return value;
  }

  public String getString(String message, String regex, String errorMessage) {
    Pattern pattern = Pattern.compile(regex);
    System.out.println(message);
    this.scanner.skip("\\R?");
    String value = this.scanner.nextLine().trim();
    if (!pattern.matcher(value).matches()) {
      System.out.println(errorMessage);
      return this.getString(message, regex, errorMessage);
    }
    return value;
  }

  public Date getDate(String message) {
    DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    System.out.println(message);
    String value = this.scanner.next().trim();
    this.scanner.nextLine();
    try {
      return format.parse(value);
    } catch (Exception e) {
      System.out.println("Write a valid date\n");
      return this.getDate(message);
    }
  }

}
