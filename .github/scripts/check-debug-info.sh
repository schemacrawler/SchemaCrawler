#!/bin/bash

# Check for javap
if ! command -v javap &> /dev/null; then
  echo "Error: javap not found. Please ensure JDK is installed and javap is in your PATH."
  exit 1
fi

# Check for argument
if [ -z "$1" ]; then
  echo "Usage: $0 path/to/YourClass.class"
  exit 1
fi

CLASSFILE="$1"

if [ ! -f "$CLASSFILE" ]; then
  echo "Error: File '$CLASSFILE' not found."
  exit 1
fi

echo "Analyzing: $CLASSFILE"
output=$(javap -v "$CLASSFILE" 2>/dev/null)

has_line=$(echo "$output" | grep -q "LineNumberTable" && echo "✅ Line numbers" || echo "❌ No line numbers")
has_vars=$(echo "$output" | grep -q "LocalVariableTable" && echo "✅ Local variables" || echo "❌ No local variables")
has_source=$(echo "$output" | grep -q "SourceFile" && echo "✅ Source file info" || echo "❌ No source file info")

echo "  $has_line"
echo "  $has_vars"
echo "  $has_source"
