databaseChangeLog = {

    changeSet(author: "kiview", id: "addOwnerColumn") {
        addColumn(tableName: "job") {
            column(name: "owner_id", type: "int8")
        }
        addForeignKeyConstraint(baseColumnNames: "owner_id", baseTableName: "job", constraintName: "fk19bbd9f95acb7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
    }

}
