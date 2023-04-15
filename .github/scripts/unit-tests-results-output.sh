#!/bin/bash
dir_name="unit_tests_results_output"
zip_file="unit_tests_results_output.zip"
temp_dir=$(mktemp -d)

find . -type d -name "$dir_name" -exec cp -r {} "$temp_dir" \;

cd "$temp_dir"
zip -r "$zip_file" .

mv "$zip_file" ..
cd ..
rm -rf "$temp_dir"
