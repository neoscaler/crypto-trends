package com.neoscaler.cryptotrends.infrastructure.database.migration;

import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import com.neoscaler.cryptotrends.database.AppDatabase;
import com.neoscaler.cryptotrends.infrastructure.configuration.module.DatabaseModule;
import java.io.IOException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MigrationTest {

  private static final String TEST_DB = "migration-test";

  @Rule
  public MigrationTestHelper helper;

  public MigrationTest() {
    helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
        AppDatabase.class.getCanonicalName(),
        new FrameworkSQLiteOpenHelperFactory());
  }

  @Test
  public void migrate2To3() throws IOException {
    SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 2);

    // db has schema version 2. insert some globalDataResultContent using SQL queries.
    // You cannot use DAO classes because they expect the latest schema.

    // Prepare for the next version.
    db.close();

    // Re-open the database with version 2 and provide
    // MIGRATION_1_2 as the migration process.
    db = helper.runMigrationsAndValidate(TEST_DB, 3, true, DatabaseModule.MIGRATION_2_3);

    // MigrationTestHelper automatically verifies the schema changes,
    // but you need to validate that the globalDataResultContent was migrated properly.
  }

  @Test
  public void migrate3To4() throws IOException {
    SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 3);
    db.close();
    helper.runMigrationsAndValidate(TEST_DB, 4, true, DatabaseModule.MIGRATION_3_4);
  }

  @Test
  public void migrate4To5() throws IOException {
    SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 4);
    db.close();
    helper.runMigrationsAndValidate(TEST_DB, 5, true, DatabaseModule.MIGRATION_4_5);
  }

  @Test
  public void migrate5To6() throws IOException {
    SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 5);
    db.close();
    helper.runMigrationsAndValidate(TEST_DB, 6, true, DatabaseModule.MIGRATION_5_6);
  }

  @Test
  public void migrate6To7() throws IOException {
    SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 6);
    db.close();
    helper.runMigrationsAndValidate(TEST_DB, 7, true, DatabaseModule.MIGRATION_6_7);
  }
}
