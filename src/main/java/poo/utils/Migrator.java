package poo.utils;

import java.io.File;
// import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;


public class Migrator {
  private Connection connection;
  private String folderPath;
  private static final Pattern FOLD_PATTERN = Pattern.compile("^\\d{3,}_[a-z0-9_]+\\.sql$", Pattern.CASE_INSENSITIVE);
  public Migrator(String folderPath, Connection connection) {
    this.connection = connection;
    this.folderPath = folderPath;
  }

  public void run() throws SQLException {
    this.runOwnerMigration();
    this.runMigrations();
    System.out.println("Migrations completed successfully!");
  }

  private ArrayList<String> listFilesForFolder(final File folder) {
    ArrayList<String> filenames = new ArrayList<>();
    File[] files = folder.listFiles();
    if (files == null) {
      return filenames;
    }
    for (final File fileEntry : files) {
      if (!fileEntry.isDirectory() && FOLD_PATTERN.matcher(fileEntry.getName()).matches()) {
        filenames.add(fileEntry.getName());
      } else {
        System.out.println("Don't match pattern(ex: 001_anyname.sql): " + fileEntry.getName());
      }
    }
    return filenames;
  }

  private void runOwnerMigration() throws SQLException {
    this.connection.createStatement()
        .execute("CREATE TABLE IF NOT EXISTS __migrations (filename VARCHAR(255), executed_at TIMESTAMP)");
  }

  private String readFile(File file) throws SQLException {
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

  private boolean checkIfAlreadyMigrated(String fileName) throws SQLException {
    PreparedStatement stmt = connection.prepareStatement("SELECT * FROM __migrations WHERE filename = ?");
    stmt.setString(1, fileName);
    return stmt.executeQuery().next();
  }

  private void addRanMigration(String fileName) throws SQLException {
    PreparedStatement stmt = connection.prepareStatement("INSERT INTO __migrations (filename, executed_at) VALUES (?, now())");
    stmt.setString(1, fileName);
    stmt.execute();
  }

  private void runMigrations() throws RuntimeException {
    final File folder = new File(this.folderPath);
    int ranMigrations = 0;
    String content = "";
    ArrayList<String> fileNames = this.listFilesForFolder(folder);
    for (String fileName : fileNames) {
      try {
        if (this.checkIfAlreadyMigrated(fileName)) {
          continue;
        }

        System.out.println("Running: " + fileName);

        content = "BEGIN TRANSACTION;\n";
        content += readFile(new File(this.folderPath + "/" + fileName));
        content += "COMMIT";

        connection.createStatement().execute(content);
        
        this.addRanMigration(fileName);
        System.out.printf("Migration %s ran successfully!\n", fileName);
        
        ranMigrations++;
      } catch (SQLException e) {
        System.out.printf("Cannot run migration %s\n", fileName);
        throw new RuntimeException(e);
      }
    }
    System.out.printf("Ran %s migrations successfully!\n", ranMigrations);
  }
  
}
