package com.openrubicon.economics.database.migrations;

import com.openrubicon.core.api.database.Connection;
import com.openrubicon.core.api.database.interfaces.DatabaseMigration;

/**
 * Created by Quinn on 12/12/2017.
 */
public class UpdateTransactionsAddDates implements DatabaseMigration {
    @Override
    public boolean up(Connection connection) {
        return connection.alterTable("ALTER TABLE `rubicon_economics_transactions` ADD `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `timestamp`, ADD `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `created_at`, ADD `deleted_at` DATETIME NULL DEFAULT NULL AFTER `updated_at`;");
    }

    @Override
    public boolean down(Connection connection) {
        return connection.alterTable("ALTER TABLE `rubicon_economics_transactions` DROP COLUMN `created_at`, DROP COLUMN `updated_at`, DROP COLUMN `deleted_at`;");
    }

    @Override
    public int getVersion() {
        return 2;
    }
}
