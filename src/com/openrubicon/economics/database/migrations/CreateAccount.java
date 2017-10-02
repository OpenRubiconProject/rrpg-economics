package com.openrubicon.economics.database.migrations;

import com.openrubicon.core.api.database.Connection;
import com.openrubicon.core.api.database.interfaces.DatabaseMigration;
import com.openrubicon.economics.database.models.AccountModel;

/**
 * Created by Quinn on 10/1/2017.
 */
public class CreateAccount implements DatabaseMigration {
    @Override
    public boolean up(Connection connection) {
        connection.createTable("CREATE TABLE IF NOT EXISTS `rubicon_economics_account` (\n" +
                " `id` int(15) NOT NULL AUTO_INCREMENT,\n" +
                " `uuid` varchar(64) NOT NULL,\n" +
                " `name` varchar(255) NOT NULL,\n" +
                " `bal` double(50,5) NOT NULL DEFAULT '0.00000',\n" +
                " PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        return true;
    }

    @Override
    public boolean down(Connection connection) {
        connection.dropTable(new AccountModel().getTableName());
        return true;
    }

    @Override
    public int getVersion() {
        return new AccountModel().getVersion();
    }
}
