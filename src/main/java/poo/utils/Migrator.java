package poo.utils;

import java.io.File;
// import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;


public class Migrator {
  private Connection connection;
  private String folderPath;
  // private static final Pattern FOLD_PATTERN = Pattern.compile("/^\\d{3,}_[a-z0-9_]+\\.sql$/i");
  public Migrator(String folderPath, Connection connection) {
    this.connection = connection;
    this.folderPath = folderPath;
  }

  public void run() throws SQLException {
    this._runOwnerMigration();
    this._runMigrations();
    System.out.println("Migrations completed successfully!");
  }

  public ArrayList<String> _listFilesForFolder(final File folder) {
    ArrayList<String> filenames = new ArrayList<>();
    File[] files = folder.listFiles();
    if (files == null) {
      return filenames;
    }
    for (final File fileEntry : files) {
      if (fileEntry.isDirectory()) {
        _listFilesForFolder(fileEntry);
      } else {
        filenames.add(fileEntry.getName());
      }
    }
    return filenames;
  }

  private void _runOwnerMigration() throws SQLException {
    this.connection.createStatement()
        .execute("CREATE TABLE IF NOT EXISTS __migrations (filename VARCHAR(255), executed_at TIMESTAMP)");
  }

  private String _readFile(File file) throws SQLException {
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

  public boolean _checkIfAlreadyMigrated(String fileName) throws SQLException {
    ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM __migrations WHERE filename = '" + fileName + "'");
    return resultSet.next();
  }

  public void _runMigrations() throws RuntimeException {
    final File folder = new File(this.folderPath);
    int ranMigrations = 0;
    String content = "";
    ArrayList<String> fileNames = this._listFilesForFolder(folder);
    for (String fileName : fileNames) {
      try {
        if (this._checkIfAlreadyMigrated(fileName)) {
          continue;
        }
        System.out.println("Running: " + fileName);
        content = "BEGIN TRANSACTION;\n";
        content += _readFile(new File(this.folderPath + "/" + fileName));
        content += "COMMIT";
        connection.createStatement().execute(content);
        connection.createStatement().execute("INSERT INTO __migrations (filename, executed_at) VALUES ('" + fileName + "', now());");
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
