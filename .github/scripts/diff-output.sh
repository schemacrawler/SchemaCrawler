#!/bin/bash

# Check for the right number of arguments
if [ "$#" -ne 3 ]; then
  echo "Usage: $0 <file1> <file2> <output_html>"
  exit 1
fi

# Assign arguments to variables
file1=$1
file2=$2
output_html=$3
temp_diff_file=$(mktemp)

# Check if files to diff exist
if [ ! -e "$file1" ]; then
  echo "$file1 does not exist. Exiting script."
  exit 1
fi
if [ ! -e "$file2" ]; then
  echo "$file2 does not exist. Exiting script."
  exit 1
fi


# Perform the diff and save to a temporary file
diff \
  --unified \
  --new-file \
  --unidirectional-new-file \
  --ignore-case \
  --ignore-all-space \
  --text \
  "$file1" \
  "$file2" \
  > "$temp_diff_file"

# Check that the output file was created
if [ ! -e "$temp_diff_file" ]; then
  echo "$temp_diff_file does not exist. Exiting script."
  exit 1
fi
# DEBUG
echo "$temp_diff_file exists. Proceeding with the script."
head "$temp_diff_file"


# Convert the diff to HTML
diff2html \
  --colorScheme light \
  --format html \
  --style side \
  --title "Diff - $file1 vs $file2" \
  --input file \
  --file "$output_html" \
  -- "$temp_diff_file"


# Clean up temporary file
rm "$temp_diff_file"
