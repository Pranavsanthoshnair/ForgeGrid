#!/bin/bash
echo "Running ForgeGrid from IDE..."

# Check if bin directory exists
if [ ! -d "bin" ]; then
    echo "Building project first..."
    ./build.sh
    if [ $? -ne 0 ]; then
        echo "Build failed!"
        exit 1
    fi
fi

# Run the application with explicit classpath
java -cp "bin:lib/sqlite-jdbc-3.44.1.0.jar:lib/slf4j-api-1.7.36.jar:lib/slf4j-simple-1.7.36.jar" com.forgegrid.app.Main
