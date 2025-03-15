package poo.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Migrator {
  private Connection connection;
  private String folderPath;
  private String tableName = "__migrations";
  private static final Pattern FILE_PATTERN = Pattern.compile("^\\d{3,}_[a-z0-9_]+\\.sql$", Pattern.CASE_INSENSITIVE);
  
  public Migrator(Connection connection, String folderPath) {
    this.connection = connection;
    this.folderPath = folderPath;
  }
  
  public Migrator(Connection connection, String folderPath, String tableName) {
    this.connection = connection;
    this.folderPath = folderPath;
    this.tableName = tableName;
  }

  public void run() throws SQLException {
    this.runOwnerMigration();
    this.runMigrations();
   Logger.println("Migrations completed successfully!");
  }

  private ArrayList<String> listFilesForFolder(final File folder) {
    ArrayList<String> filenames = new ArrayList<>();
    File[] files = folder.listFiles();
    if (files == null) {
      return filenames;
    }
    for (final File fileEntry : files) {
      if (!fileEntry.isDirectory() && FILE_PATTERN.matcher(fileEntry.getName()).matches()) {
        filenames.add(fileEntry.getName());
      } else {
       Logger.println("Don't match pattern(ex: 001_anyname.sql): " + fileEntry.getName());
      }
    }
    return filenames;
  }

  private void runOwnerMigration() throws SQLException {
    String sql = "CREATE TABLE IF NOT EXISTS " + this.tableName + " (filename VARCHAR(255), executed_at TIMESTAMP)";
    PreparedStatement stmt = this.connection.prepareStatement(sql);
    stmt.execute();
    stmt.close();
  }

  private String readFile(File file) throws RuntimeException {
    try {
      Scanner scanner = new Scanner(file);
      String result = "";
      while (scanner.hasNextLine()) {
        result += scanner.nextLine() + "\n";
      }
      scanner.close();
      return result;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private ArrayList<String> getFilesAlreadyMigrated() throws SQLException {
    String sql = "SELECT * FROM " + this.tableName;
    PreparedStatement stmt = this.connection.prepareStatement(sql);
    ResultSet rs = stmt.executeQuery();
    ArrayList<String> files = new ArrayList<>();
    while (rs.next())
      files.add(rs.getString("filename"));
    stmt.close();
    return files;
  }

  private void addRanMigration(String fileName) throws SQLException {
    String sql = "INSERT INTO " + this.tableName + " (filename, executed_at) VALUES (?, now())";
    PreparedStatement stmt = this.connection.prepareStatement(sql);
    stmt.setString(1, fileName);
    stmt.execute();
    stmt.close();
  }

  private void runMigrations() throws SQLException {
    final File folder = new File(this.folderPath);
    int ranMigrations = 0;
    String content = "";
    ArrayList<String> filesAlreadyMigrated = this.getFilesAlreadyMigrated();
    ArrayList<String> fileNames = this.listFilesForFolder(folder);
    for (String fileName : fileNames) {
      try {
        if (filesAlreadyMigrated.contains(fileName)) {
          continue;
        }

       Logger.println("Running: " + fileName);

        content = "BEGIN TRANSACTION;\n";
        content += readFile(new File(this.folderPath + "/" + fileName));
        content += "COMMIT";

        connection.createStatement().execute(content);
        
        this.addRanMigration(fileName);
        System.out.printf("Migration %s ran successfully!\n", fileName);
        
        ranMigrations++;
      } catch (SQLException e) {
       Logger.println("Cannot run migration " + fileName + ": " + e.getMessage());
      }
    }
    Logger.printf("Ran %s migrations successfully!\n", ranMigrations);
  }
  
}
