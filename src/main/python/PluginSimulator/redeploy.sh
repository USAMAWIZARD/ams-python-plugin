set -e
cd .. 
python setup.py build_ext --inplace
cd ./PluginSimulator/
make
