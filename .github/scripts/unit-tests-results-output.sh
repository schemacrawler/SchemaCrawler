#!/bin/bash

dir_name="unit_tests_results_output"
zip_file="$1/unit_tests_results_output.zip"

find . -type d -name "$dir_name" -exec zip --verbose --display-usize --recurse-paths --grow "$zip_file" {} \; ;
