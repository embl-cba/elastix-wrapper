#!/bin/bash
ELASTIX_PATH=/Applications/elastix-5.2.0-mac/
export DYLD_LIBRARY_PATH=$ELASTIX_PATH/lib/
$ELASTIX_PATH/bin/transformix $@
