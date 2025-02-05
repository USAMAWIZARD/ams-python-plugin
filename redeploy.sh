#!/bin/sh
AMS_DIR=/usr/local/antmedia/
mvn clean install -Dmaven.javadoc.skip=true -Dmaven.test.skip=true -Dgpg.skip=true
OUT=$?

if [ $OUT -ne 0 ]; then
    exit $OUT
fi

cd ./src/main/python/
python setup.py build_ext --inplace
cd ../../../

cp ./src/main/python/libpythonWrapper.cpython-313-x86_64-linux-gnu.so /usr/local/antmedia/lib/native-linux-x86_64/

rm -r $AMS_DIR/plugins/pythonplugin*
cp target/pythonplugin.jar $AMS_DIR/plugins/

OUT=$?

if [ $OUT -ne 0 ]; then
    exit $OUT
fi
cd $AMS_DIR
./start-debug.sh
