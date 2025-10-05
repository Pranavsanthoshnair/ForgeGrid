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
    echo "You can now run the application from your IDE:"
    echo "- Right-click on Main.java"
    echo "- Select \"Run Java\""
else
    echo "Build failed!"
fi
