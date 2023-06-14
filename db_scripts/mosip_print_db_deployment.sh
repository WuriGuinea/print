### -- ---------------------------------------------------------------------------------------------------------
### -- Script Name		: MOSIP ALL DB Artifacts deployment script
### -- Deploy Module 	: MOSIP Print Module
### -- Purpose    		: To deploy MOSIP Registration Module Database DB Artifacts.       
### -- Create By   		: Thamaraikannan
### -- Created Date		: 24-Dec-2021
### -- 
### -- Modified Date        Modified By         Comments / Remarks
### -- -----------------------------------------------------------------------------------------------------------

#! bin/bash
echo "`date` : You logged on to DB deplyment server as : `whoami`"
echo "`date` : MOSIP Database objects deployment started...."

echo "=============================================================================================================="
bash ./mosip_print/mosip_print_db_deploy.sh ./mosip_print/mosip_print_deploy.properties
echo "=============================================================================================================="

echo "`date` : MOSIP DB Deployment for print module databases is completed, Please check the logs at respective logs directory for more information"
 
