package com.openrubicon.economics.database.migrations;

import com.openrubicon.core.api.database.Connection;
import com.openrubicon.core.api.database.interfaces.DatabaseMigration;
import com.openrubicon.economics.database.models.TransactionModel;

/**
 * Created by Quinn on 10/1/2017.
 */
public class CreateTransaction implements DatabaseMigration {
    @Override
    public boolean up(Connection connection) {
        connection.createTable("CREATE TABLE `rubicon_economics_transactions` (\n" +
                " `id` int(64) NOT NULL AUTO_INCREMENT,\n" +
                " `decreasedUuid` varchar(255) NOT NULL DEFAULT ' ',\n" +
                " `increasedUuid` varchar(255) NOT NULL DEFAULT ' ',\n" +
                " `amount` int(65) NOT NULL DEFAULT '0',\n" +
                " `comment` varchar(255) NOT NULL,\n" +
                " `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                " PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4");
        return true;
    }

    @Override
    public boolean down(Connection connection) {
        connection.dropTable(new TransactionModel().getTableName());
        return false;
    }

    @Override
    public int getVersion() {
        return new TransactionModel().getVersion();
    }
}
