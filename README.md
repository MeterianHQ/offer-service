# offer-service  
# Simple service handle operations with offers 

# Command to build out application .jar file
./service.sh dev_build

# Command to create offers DB in Postgresql. Please create offers_db schema in offers_engine database.
./service.sh db_create

# Command to run application
./service.sh dev_run

# Command to run application
./service.sh dev_run_debugs

# Command packages application into a docker image
./service.sh docker_build

# Command runs application using a docker image
./service.sh docker_run

# Command to set up environment and deploy to UAT and PROd
1. Make sure you have Terraform installed in your machine
2. Go to directory 'environment'
3. Export the access and secret key of the environment you want to deploy on in 'AWS_ACCESS_KEY_ID' and 'AWS_SECRET_ACCESS_KEY' environment variables
4. Run the following to initialize the state:
   ```bash
   terraform init -backend-config "bucket=<bucket name>"
   ```
   where `<bucket name>` is the name of the bucket where the terraform state is stored in S3 (either 'offer-uat-terraform-state' or 'offer-prod-terraform-state')
5. If the state initialization was successful, run:
   ```bash
   terraform plan -var-file=./application-variables/variables-<env>.tfvars -var "offer_service_version=<version_to_deploy>"
   ```
   where `<env>` can be either `uat` or `prod`.   
6. If the output of the previous step looks reasonable, run:
   ```bash
   terraform apply -var-file=./application-variables/variables-<env>.tfvars -var "offer_service_version=<version_to_deploy>"
   ```
   where `<env>` can be either `uat` or `prod`.