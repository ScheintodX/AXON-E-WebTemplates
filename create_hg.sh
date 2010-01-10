#!/usr/bin/bash

hg init
hg add create_hg.sh src WebContent lib .classpath .hgignore .project .settings
hg commit -m 'First import'

