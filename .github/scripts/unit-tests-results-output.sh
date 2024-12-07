#!/bin/bash

dir_name="unit_tests_results_output"
zip_file="$1/unit_tests_results_output.zip"

# Zip all expected results directories
find . -type d -name "$dir_name" -exec zip --verbose --display-usize --recurse-paths --grow "$zip_file" {} \; ;

# Check if the zip file was created and print the appropriate message
if [[ -f "$zip_file" ]]; then
  echo "Expected results in zip file: $(realpath "$zip_file")"
else
  echo "Expected results zip file not created"
fi
