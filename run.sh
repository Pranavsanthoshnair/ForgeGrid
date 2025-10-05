#!/bin/bash

echo "Running ForgeGrid..."

# Check if bin directory exists
if [ ! -d "bin" ]; then
    echo "Bin directory not found. Please run ./build.sh first."
    exit 1
fi

# Run the application
java -cp "bin:lib/*" com.forgegrid.app.Main
