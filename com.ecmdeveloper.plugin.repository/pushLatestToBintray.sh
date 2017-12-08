#!/bin/bash
#Sample Usage: pushToBintray.sh username apikey owner repo package version pathToP2Repo
API=https://api.bintray.com
BINTRAY_USER=$1
BINTRAY_API_KEY=$2
BINTRAY_OWNER="ecmdeveloper"
BINTRAY_REPO="eclipse-plugin"
PCK_NAME="ecmdeveloper"
PCK_VERSION=$3
PATH_TO_REPOSITORY="latest"

function main() {
deploy_updatesite
}

function deploy_updatesite() {
echo "${BINTRAY_USER}"
echo "${BINTRAY_API_KEY}"
echo "${BINTRAY_OWNER}"
echo "${BINTRAY_REPO}"
echo "${PCK_NAME}"
echo "${PCK_VERSION}"
echo "${PATH_TO_REPOSITORY}"

if [ ! -z "$PATH_TO_REPOSITORY" ]; then
   cd $PATH_TO_REPOSITORY
   if [ $? -ne 0 ]; then
     #directory does not exist
     echo $PATH_TO_REPOSITORY " does not exist"
     exit 1
   fi
fi

#curl -X DELETE -u ${BINTRAY_USER}:${BINTRAY_API_KEY} https://api.bintray.com/content/${BINTRAY_OWNER}/${BINTRAY_REPO}/ecmdeveloper/2.3.0/compositeArtifacts.xml
#curl -X DELETE -u ${BINTRAY_USER}:${BINTRAY_API_KEY} https://api.bintray.com/content/${BINTRAY_OWNER}/${BINTRAY_REPO}/ecmdeveloper/2.3.0/compositeContent.xml
#curl -X DELETE -u ${BINTRAY_USER}:${BINTRAY_API_KEY} https://api.bintray.com/content/${BINTRAY_OWNER}/${BINTRAY_REPO}/ecmdeveloper/2.3.0/p2.index

curl -X DELETE -u ${BINTRAY_USER}:${BINTRAY_API_KEY} https://api.bintray.com/content/${BINTRAY_OWNER}/${BINTRAY_REPO}/artifacts.jar
curl -X DELETE -u ${BINTRAY_USER}:${BINTRAY_API_KEY} https://api.bintray.com/content/${BINTRAY_OWNER}/${BINTRAY_REPO}/content.jar

FILES=./*

for f in $FILES;
do
if [ ! -d $f ]; then
  echo "Processing $f file..."
#  curl -X PUT -T $f -u ${BINTRAY_USER}:${BINTRAY_API_KEY} https://api.bintray.com/content/${BINTRAY_OWNER}/${BINTRAY_REPO}/${PCK_NAME}/latest/$f;publish=0
  echo ""
fi
done

#echo "Publishing the new version"
#curl -X POST -u ${BINTRAY_USER}:${BINTRAY_API_KEY} https://api.bintray.com/content/${BINTRAY_OWNER}/${BINTRAY_REPO}/${PCK_NAME}/${PCK_VERSION}/publish -d "{ \"discard\": \"false\" }"
#
}


main "$@"
