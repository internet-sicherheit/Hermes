databaseChangeLog = {

	changeSet(author: "kiview (generated)", id: "1406209507884-1") {
		createTable(tableName: "hypervisor") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hypervisorPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-2") {
		createTable(tableName: "job") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "jobPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "error_log", type: "varchar(255)")

			column(name: "log_file_name", type: "varchar(255)")

			column(name: "memory_dump", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "node_id", type: "int8")

			column(name: "priority", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "publishing_date", type: "timestamp")

			column(name: "report_file_name", type: "varchar(255)")

			column(name: "sample_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "sensor_id", type: "int8")

			column(name: "simulated_time", type: "timestamp")

			column(name: "start_time", type: "timestamp")

			column(name: "state", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "timeout", type: "time") {
				constraints(nullable: "false")
			}

			column(name: "virtual_machine_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-3") {
		createTable(tableName: "node") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "nodePK")
			}

			column(name: "current_state", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "metadata", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "network_address", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-4") {
		createTable(tableName: "node_hypervisor") {
			column(name: "node_hypervisors_id", type: "int8")

			column(name: "hypervisor_id", type: "int8")
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-5") {
		createTable(tableName: "node_status_data") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "node_status_dPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "cpu_load_avg1", type: "float4") {
				constraints(nullable: "false")
			}

			column(name: "cpu_load_avg15", type: "float4") {
				constraints(nullable: "false")
			}

			column(name: "cpu_load_avg5", type: "float4") {
				constraints(nullable: "false")
			}

			column(name: "node_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "timestamp", type: "timestamp") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-6") {
		createTable(tableName: "operating_system") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "operating_sysPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)")

			column(name: "meta", type: "varchar(255)")

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "type", type: "varchar(255)")
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-7") {
		createTable(tableName: "role") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "rolePK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "authority", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-8") {
		createTable(tableName: "sample") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "samplePK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)")

			column(name: "file_content_type", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "file_extension", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "md5", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "original_filename", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "sha1", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "sha256", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "sha512", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-9") {
		createTable(tableName: "sensor") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "sensorPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)")

			column(name: "md5", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "original_filename", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "type", type: "varchar(255)")
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-10") {
		createTable(tableName: "user") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "userPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "account_expired", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "account_locked", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "email", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "enabled", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "password", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "password_expired", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "username", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-11") {
		createTable(tableName: "user_role") {
			column(name: "role_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-12") {
		createTable(tableName: "virtual_machine") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "virtual_machiPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "description", type: "varchar(255)")

			column(name: "hypervisor_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "name", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "original_filename", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "os_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-13") {
		addPrimaryKey(columnNames: "role_id, user_id", constraintName: "user_rolePK", tableName: "user_role")
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-25") {
		createIndex(indexName: "name_uniq_1406209507721", tableName: "node", unique: "true") {
			column(name: "name")
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-26") {
		createIndex(indexName: "authority_uniq_1406209507727", tableName: "role", unique: "true") {
			column(name: "authority")
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-27") {
		createIndex(indexName: "email_uniq_1406209507700", tableName: "user", unique: "true") {
			column(name: "email")
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-28") {
		createIndex(indexName: "username_uniq_1406209507705", tableName: "user", unique: "true") {
			column(name: "username")
		}
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-29") {
		createSequence(sequenceName: "hibernate_sequence")
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-30") {
		createSequence(sequenceName: "job_sequence")
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-14") {
		addForeignKeyConstraint(baseColumnNames: "node_id", baseTableName: "job", constraintName: "FK19BBD7C367E2C", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "node", referencesUniqueColumn: "false")
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-15") {
		addForeignKeyConstraint(baseColumnNames: "sample_id", baseTableName: "job", constraintName: "FK19BBD7C51A2DC", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "sample", referencesUniqueColumn: "false")
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-16") {
		addForeignKeyConstraint(baseColumnNames: "sensor_id", baseTableName: "job", constraintName: "FK19BBD564428DC", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "sensor", referencesUniqueColumn: "false")
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-17") {
		addForeignKeyConstraint(baseColumnNames: "virtual_machine_id", baseTableName: "job", constraintName: "FK19BBD504137E5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "virtual_machine", referencesUniqueColumn: "false")
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-18") {
		addForeignKeyConstraint(baseColumnNames: "hypervisor_id", baseTableName: "node_hypervisor", constraintName: "FK6D55657482268EBC", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "hypervisor", referencesUniqueColumn: "false")
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-19") {
		addForeignKeyConstraint(baseColumnNames: "node_hypervisors_id", baseTableName: "node_hypervisor", constraintName: "FK6D5565744EE1D58F", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "node", referencesUniqueColumn: "false")
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-20") {
		addForeignKeyConstraint(baseColumnNames: "node_id", baseTableName: "node_status_data", constraintName: "FKDCEB191A7C367E2C", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "node", referencesUniqueColumn: "false")
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-21") {
		addForeignKeyConstraint(baseColumnNames: "role_id", baseTableName: "user_role", constraintName: "FK143BF46A8E8438BF", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "role", referencesUniqueColumn: "false")
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-22") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "user_role", constraintName: "FK143BF46A33AEFC9F", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", referencesUniqueColumn: "false")
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-23") {
		addForeignKeyConstraint(baseColumnNames: "hypervisor_id", baseTableName: "virtual_machine", constraintName: "FKA8EBCF1382268EBC", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "hypervisor", referencesUniqueColumn: "false")
	}

	changeSet(author: "kiview (generated)", id: "1406209507884-24") {
		addForeignKeyConstraint(baseColumnNames: "os_id", baseTableName: "virtual_machine", constraintName: "FKA8EBCF13F273CD04", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "operating_system", referencesUniqueColumn: "false")
	}

	include file: 'owner.groovy'
}
