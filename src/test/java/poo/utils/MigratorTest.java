package poo.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MigratorTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private Statement statement;

    @TempDir
    Path tempDir;

    private Migrator migrator;
    private String migrationsPath;

    @BeforeEach
    void setUp() throws SQLException {
        migrationsPath = tempDir.toString();
        migrator = new Migrator(connection, migrationsPath);
        
        lenient().when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        lenient().when(connection.createStatement()).thenReturn(statement);
        lenient().when(preparedStatement.execute()).thenReturn(false);
        lenient().when(preparedStatement.executeQuery()).thenReturn(resultSet);
        lenient().when(resultSet.next()).thenReturn(false);
    }

    @Test
    @DisplayName("Should create migrations table if it doesn't exist")
    void shouldCreateMigrationsTable() throws SQLException {
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        migrator.run();

        verify(connection, atLeastOnce()).prepareStatement(sqlCaptor.capture());
        boolean containsCreateTable = sqlCaptor.getAllValues().stream()
                .anyMatch(sql -> sql.contains("CREATE TABLE IF NOT EXISTS __migrations"));
        assertTrue(containsCreateTable, "Nenhuma instrução SQL contém CREATE TABLE IF NOT EXISTS __migrations");
    }

    @Test
    @DisplayName("Should use custom table name when provided")
    void shouldUseCustomTableName() throws SQLException {
        migrator = new Migrator(connection, migrationsPath, "custom_migrations");
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);

        migrator.run();

        verify(connection, atLeastOnce()).prepareStatement(sqlCaptor.capture());
        boolean containsCreateTable = sqlCaptor.getAllValues().stream()
                .anyMatch(sql -> sql.contains("CREATE TABLE IF NOT EXISTS custom_migrations"));
        assertTrue(containsCreateTable, "Nenhuma instrução SQL contém CREATE TABLE IF NOT EXISTS custom_migrations");

    }

    @Test
    @DisplayName("Should skip migrations that have already been executed")
    void shouldSkipExecutedMigrations() throws SQLException, IOException {
        createMigrationFile("001_create_table.sql", "CREATE TABLE test (id INT);");

        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("filename")).thenReturn("001_create_table.sql");

        migrator.run();

        verify(statement, never()).execute(contains("CREATE TABLE test"));
    }

    @Test
    @DisplayName("Should execute new migrations")
    void shouldExecuteNewMigrations() throws SQLException, IOException {
        createMigrationFile("001_create_table.sql", "CREATE TABLE test (id INT);");

        when(resultSet.next()).thenReturn(false);

        migrator.run();

        verify(statement).execute(contains("CREATE TABLE test"));

        ArgumentCaptor<String> insertSqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(connection, atLeast(2)).prepareStatement(insertSqlCaptor.capture());
        assertTrue(insertSqlCaptor.getAllValues().stream()
                .anyMatch(sql -> sql.contains("INSERT INTO __migrations")));

        ArgumentCaptor<String> fileNameCaptor = ArgumentCaptor.forClass(String.class);
        verify(preparedStatement, atLeast(1)).setString(eq(1), fileNameCaptor.capture());
        assertEquals("001_create_table.sql", fileNameCaptor.getValue());
    }

    @Test
    @DisplayName("Should handle multiple migrations in correct order")
    void shouldHandleMultipleMigrations() throws SQLException, IOException {
        createMigrationFile("001_first_migration.sql", "CREATE TABLE first (id INT);");
        createMigrationFile("002_second_migration.sql", "CREATE TABLE second (id INT);");

        when(resultSet.next()).thenReturn(false);

        migrator.run();

        verify(statement).execute(contains("CREATE TABLE first"));
        verify(statement).execute(contains("CREATE TABLE second"));
    }

    @Test
    @DisplayName("Should ignore files that don't match migration pattern")
    void shouldIgnoreNonMigrationFiles() throws SQLException, IOException {
        createMigrationFile("001_valid_migration.sql", "CREATE TABLE valid (id INT);");
        createFile("invalid_file.txt", "This is not a migration");

        when(resultSet.next()).thenReturn(false);

        migrator.run();

        verify(statement, times(1)).execute(anyString());
        verify(statement).execute(contains("CREATE TABLE valid"));
    }

    @Test
    @DisplayName("Should continue with other migrations if one fails")
    void shouldContinueAfterFailedMigration() throws SQLException, IOException {
        createMigrationFile("001_failing_migration.sql", "INVALID SQL;");
        createMigrationFile("002_valid_migration.sql", "CREATE TABLE valid (id INT);");

        when(resultSet.next()).thenReturn(false);

        when(statement.execute(contains("INVALID SQL"))).thenThrow(new SQLException("Invalid SQL"));

        migrator.run();

        verify(statement).execute(contains("CREATE TABLE valid"));
    }

    private void createMigrationFile(String fileName, String content) throws IOException {
        createFile(fileName, content);
    }

    private void createFile(String fileName, String content) throws IOException {
        File file = new File(tempDir.toFile(), fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
}
