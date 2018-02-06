# offers-db-scripts
Module contains all change logs and scripts for Liquibase manipulations with offers_db

You have 2 options:
- Run from command line with parameters:

    gradle #task_name# -DuserName= -Dpassword= -Durl=

- You may not specify parameters for local environment.
    Make sure you have environment  variables:
        RDS_OFFERS_DB_URL;
        RDS_OFFERS_DB_USERNAME;
        RDS_OFFERS_DB_PASSWORD.

    gradle #task_name#

#task_name# should be replaced by some gradle task name, or they can be combined in some way.

-----------------------
Destroy all DB objects
-----------------------

#task_name# liquibaseDropAll

-----------------------
Create DB offers_db
-----------------------

#task_name# liquibaseCreate

-----------------------
Clean up all DB tables
-----------------------

#task_name# liquibaseCleanData