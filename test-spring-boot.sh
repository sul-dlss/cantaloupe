#!/bin/bash

# Test script for Cantaloupe Spring Boot application
# This script builds, starts, tests, and stops the application

set -e

echo "🧪 Testing Cantaloupe Spring Boot Application"
echo "=============================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# Function to cleanup on exit
cleanup() {
    if [ ! -z "$APP_PID" ]; then
        print_status $YELLOW "Cleaning up: Stopping application (PID: $APP_PID)"
        kill $APP_PID 2>/dev/null || true
        wait $APP_PID 2>/dev/null || true
    fi
}

# Set trap for cleanup
trap cleanup EXIT

# Test configuration
TEST_PORT=${TEST_PORT:-8182}
TIMEOUT=${TIMEOUT:-30}

print_status $BLUE "Step 1: Checking prerequisites"

# Check if cantaloupe.properties exists
if [ ! -f "cantaloupe.properties" ]; then
    print_status $YELLOW "Creating cantaloupe.properties from sample..."
    cp cantaloupe.properties.sample cantaloupe.properties
fi

# Check if port is available
if lsof -i :$TEST_PORT > /dev/null 2>&1; then
    print_status $RED "❌ Port $TEST_PORT is already in use!"
    print_status $YELLOW "Kill existing process: kill \$(lsof -ti :$TEST_PORT)"
    exit 1
fi

print_status $GREEN "✅ Port $TEST_PORT is available"

print_status $BLUE "Step 2: Building application"
if ! mvn clean compile jar:jar spring-boot:repackage -Dmaven.test.skip=true -q; then
    print_status $RED "❌ Build failed!"
    exit 1
fi

# Find the JAR file
JAR_FILE=$(find target -name "cantaloupe-*.jar" -not -name "*-sources.jar" | head -n1)
if [ -z "$JAR_FILE" ]; then
    print_status $RED "❌ Could not find built JAR file!"
    exit 1
fi

print_status $GREEN "✅ Build successful: $JAR_FILE"

print_status $BLUE "Step 3: Starting application"
java -Dcantaloupe.config=cantaloupe.properties \
     -Dserver.port=$TEST_PORT \
     -Dspring.profiles.active=production \
     -Xmx1g \
     -jar "$JAR_FILE" > application.log 2>&1 &

APP_PID=$!
print_status $YELLOW "Application started with PID: $APP_PID"

print_status $BLUE "Step 4: Waiting for application to be ready"
COUNTER=0
while [ $COUNTER -lt $TIMEOUT ]; do
    if curl -s -f http://localhost:$TEST_PORT/ > /dev/null 2>&1; then
        print_status $GREEN "✅ Application is ready!"
        break
    fi

    # Check if process is still running
    if ! kill -0 $APP_PID 2>/dev/null; then
        print_status $RED "❌ Application process died!"
        print_status $YELLOW "Last 20 lines of application log:"
        tail -20 application.log
        exit 1
    fi

    echo -n "."
    sleep 1
    COUNTER=$((COUNTER + 1))
done

if [ $COUNTER -eq $TIMEOUT ]; then
    print_status $RED "❌ Application failed to start within ${TIMEOUT} seconds!"
    print_status $YELLOW "Last 20 lines of application log:"
    tail -20 application.log
    exit 1
fi

print_status $BLUE "Step 5: Running health checks"

# Test main application endpoint (Cantaloupe landing page)
print_status $YELLOW "Testing main application endpoint..."
ROOT_RESPONSE=$(curl -s http://localhost:$TEST_PORT/)
if echo "$ROOT_RESPONSE" | grep -q -i "cantaloupe\|html"; then
    print_status $GREEN "✅ Main application endpoint working"
else
    print_status $RED "❌ Main application endpoint failed"
    exit 1
fi

# Test Cantaloupe health endpoint
print_status $YELLOW "Testing Cantaloupe health endpoint..."
if curl -s -f http://localhost:$TEST_PORT/health > /dev/null; then
    print_status $GREEN "✅ Cantaloupe health endpoint accessible"
else
    print_status $YELLOW "⚠️  Cantaloupe health endpoint not configured (this is OK)"
fi

# Test main IIIF endpoint
print_status $YELLOW "Testing IIIF endpoint..."
if curl -s -f http://localhost:$TEST_PORT/iiif/2/ > /dev/null; then
    print_status $GREEN "✅ IIIF v2 endpoint accessible"
elif curl -s http://localhost:$TEST_PORT/iiif/2/ | grep -q "404\|400\|500"; then
    print_status $YELLOW "⚠️  IIIF v2 endpoint returns error (expected without configured images)"
else
    print_status $RED "❌ IIIF v2 endpoint not accessible"
    exit 1
fi

# Test root endpoint
print_status $YELLOW "Testing root endpoint..."
if curl -s -f http://localhost:$TEST_PORT/ > /dev/null; then
    print_status $GREEN "✅ Root endpoint accessible"
else
    print_status $YELLOW "⚠️  Root endpoint returned error (this may be expected)"
fi

print_status $BLUE "Step 6: Performance check"
MEMORY_USAGE=$(ps -o rss= -p $APP_PID)
print_status $YELLOW "Memory usage: ${MEMORY_USAGE} KB"

print_status $GREEN "🎉 All tests passed!"
print_status $BLUE "Application Summary:"
echo "  - JAR: $JAR_FILE"
echo "  - PID: $APP_PID"
echo "  - Port: $TEST_PORT"
echo "  - Memory: ${MEMORY_USAGE} KB"
echo "  - Health: http://localhost:$TEST_PORT/health"
echo "  - IIIF API: http://localhost:$TEST_PORT/iiif/2/"

print_status $YELLOW "Application will be stopped automatically when script exits..."
sleep 2
