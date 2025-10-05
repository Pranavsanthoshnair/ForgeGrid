#!/bin/bash

echo "Building ForgeGrid..."

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile Java source files with Java 17 compatibility
javac -cp "lib/*" -d bin --release 17 src/main/java/com/forgegrid/app/*.java src/main/java/com/forgegrid/auth/*.java src/main/java/com/forgegrid/config/*.java src/main/java/com/forgegrid/db/*.java src/main/java/com/forgegrid/model/*.java src/main/java/com/forgegrid/ui/*.java src/main/java/com/forgegrid/ui/components/*.java

# Copy resources to bin directory
cp -r src/main/resources/* bin/

if [ $? -eq 0 ]; then
    echo "Build successful!"
    echo ""
    echo "To run the application, use: ./run.sh"
else
    echo "Build failed!"
    exit 1
fi
