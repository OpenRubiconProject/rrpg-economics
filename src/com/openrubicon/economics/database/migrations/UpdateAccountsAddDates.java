package com.openrubicon.economics.database.migrations;

import com.openrubicon.core.api.database.Connection;
import com.openrubicon.core.api.database.interfaces.DatabaseMigration;

public class UpdateAccountsAddDates implements DatabaseMigration {
    @Override
    public boolean up(Connection connection) {
        return connection.alterTable("ALTER TABLE `rubicon_economics_account` ADD `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `bal`, ADD `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_at`, ADD `deleted_at` DATETIME NULL DEFAULT NULL AFTER `updated_at`;");
    }

    @Override
    public boolean down(Connection connection) {
        return connection.alterTable("ALTER TABLE `rubicon_economics_account` DROP COLUMN `created_at`, DROP COLUMN `updated_at`, DROP COLUMN `deleted_at`;");
    }

    @Override
    public int getVersion() {
        return 2;
    }
}
