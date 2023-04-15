#!/bin/bash
dir_name="unit_tests_results_output"
zip_file="unit_tests_results_output.zip"
temp_dir=$(mktemp -d)

# Use the find command to search for directories with the given name,
# and copy each directory to the temporary directory.
if ! find . -type d -name "$dir_name" -exec cp -r {} "$temp_dir" \; ; then
  # If there are no matching directories, create an empty zip file.
  touch "$zip_file"
else
  # If there are matching directories, create a zip file containing the directories.
  cd "$temp_dir" || exit
  zip -r "$zip_file" .
  mv "$zip_file" ..
fi

# Clean up the temporary directory.
cd ..
rm -rf "$temp_dir"
