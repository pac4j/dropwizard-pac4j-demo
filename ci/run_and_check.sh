#!/bin/bash

# Script to launch dropwizard-pac4j-demo and verify it works
# Usage: ./run_and_check.sh

set -e  # Stop script on error

echo "🚀 Starting dropwizard-pac4j-demo..."

# Go to project directory (one level up from ci/)
cd ..

# Clean and compile project
echo "📦 Compiling project..."
mvn clean compile -q

# Ensure target directory exists
mkdir -p target

# Start Dropwizard server in background
echo "🌐 Starting Dropwizard server..."
mvn exec:exec > target/server.log 2>&1 &
SERVER_PID=$!

# Wait for server to start (maximum 60 seconds)
echo "⏳ Waiting for server startup..."
for i in {1..60}; do
    if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080 | grep -q "200"; then
        echo "✅ Server started successfully!"
        break
    fi
    if [ $i -eq 60 ]; then
        echo "❌ Timeout: Server did not start within 60 seconds"
        echo "📋 Server logs:"
        cat target/server.log
        kill $SERVER_PID 2>/dev/null || true
        exit 1
    fi
    sleep 1
done

# Verify application responds correctly
echo "🔍 Verifying HTTP response..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080)

if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ Application responds with HTTP 200"
    echo "🌐 Application accessible at: http://localhost:8080"

    # Default flags
    FORM_AUTH_PASSED=false
    
    # Test form authentication
    echo "🔗 Testing form authentication..."
    
    # Get the form login URL
    FORM_URL="http://localhost:8080/form/index.html"
    echo "📍 Accessing form login: $FORM_URL"
    
    # Follow redirections and capture final URL and response
    FORM_RESPONSE=$(curl -s -L -w "FINAL_URL:%{url_effective}\nHTTP_CODE:%{http_code}" "$FORM_URL")
    FORM_HTTP_CODE=$(echo "$FORM_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
    FORM_FINAL_URL=$(echo "$FORM_RESPONSE" | grep "FINAL_URL:" | cut -d: -f2-)
    FORM_CONTENT=$(echo "$FORM_RESPONSE" | sed '/^FINAL_URL:/d' | sed '/^HTTP_CODE:/d')
    
    echo "🌐 Final URL: $FORM_FINAL_URL"
    echo "📄 HTTP Code: $FORM_HTTP_CODE"
    
    # Verify we reached the form login page
    if [ "$FORM_HTTP_CODE" = "200" ] && echo "$FORM_CONTENT" | grep -q "name=\"username\""; then
        echo "✅ Form login page test passed!"
        echo "🔐 Successfully reached form login page"
        echo "📋 Page contains login form with username/password fields"
        FORM_TEST_PASSED=true

        # Simulate a form login using curl WITH cookies and follow redirects
        echo "🧪 Simulating form authentication via curl (with cookies, follow redirects)..."
        COOKIE_JAR="target/form_cookies.txt"
        FORM_LOGIN_PAGE="target/form_login.html"
        FORM_AFTER_LOGIN="target/form_after_login.html"
        FINAL_APP_PAGE="target/final_app.html"

        # 1) Save the form page content
        echo "⬇️  Saving form login page..."
        echo "$FORM_CONTENT" > "$FORM_LOGIN_PAGE"

        # 2) Extract the callback URL from the form action
        CALLBACK_URL=$(echo "$FORM_CONTENT" | grep -o 'action="[^"]*"' | sed 's/action="//;s/"//' | head -n1 || true)
        if [ -z "$CALLBACK_URL" ]; then
            CALLBACK_URL="/callback?client_name=FormClient"
        fi
        
        # Decode HTML entities
        CALLBACK_URL=$(echo "$CALLBACK_URL" | sed 's/&#61;/=/g')
        
        # Make the URL absolute
        if [[ "$CALLBACK_URL" != http* ]]; then
            CALLBACK_URL="http://localhost:8080$CALLBACK_URL"
        fi
        
        echo "🔑 Form callback URL: $CALLBACK_URL"

        # 3) Post credentials to the form callback URL
        echo "📤 Posting form credentials..."
        FORM_POST_RESPONSE=$(curl -s -c "$COOKIE_JAR" -b "$COOKIE_JAR" -L -o "$FORM_AFTER_LOGIN" -w "FINAL_URL:%{url_effective}\nHTTP_CODE:%{http_code}" \
            --data-urlencode "username=jerome" \
            --data-urlencode "password=jerome" \
            --data-urlencode "submit=Submit" \
            "$CALLBACK_URL")

        FORM_POST_HTTP_CODE=$(echo "$FORM_POST_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
        FORM_POST_FINAL_URL=$(echo "$FORM_POST_RESPONSE" | grep "FINAL_URL:" | cut -d: -f2-)

        echo "🌐 After login final URL: $FORM_POST_FINAL_URL"
        echo "📄 HTTP Code: $FORM_POST_HTTP_CODE"

        # 4) Fetch the final app page with cookies and show content
        echo "📥 Fetching final app page content..."
        # Try the home page first, as /views/ might not exist
        HOME_URL="http://localhost:8080/"
        FINAL_META=$(curl -s -c "$COOKIE_JAR" -b "$COOKIE_JAR" -L -o "$FINAL_APP_PAGE" -w "FINAL_URL:%{url_effective}\nHTTP_CODE:%{http_code}" "$HOME_URL")
        FINAL_URL=$(echo "$FINAL_META" | grep "FINAL_URL:" | cut -d: -f2-)
        FINAL_APP_CODE=$(echo "$FINAL_META" | grep "HTTP_CODE:" | cut -d: -f2)

        echo "🌐 Final app URL after redirects: $FINAL_URL"
        echo "📄 Final app HTTP Code: $FINAL_APP_CODE"

        if [ "$FINAL_APP_CODE" = "200" ]; then
            echo "✅ Demo reachable after form login (HTTP 200)"
            FORM_AUTH_PASSED=true
            echo "----- Final page content (begin) -----"
            cat "$FINAL_APP_PAGE"
            echo "\n----- Final page content (end) -----"

            # Verify that the expected authenticated user is present in the page
            if grep -q "jerome" "$FINAL_APP_PAGE"; then
                echo "✅ Username 'jerome' found in final page content"
            else
                echo "❌ Username 'jerome' NOT found in final page content"
                FORM_AUTH_PASSED=false
            fi
        else
            echo "❌ Demo not reachable after form login (HTTP $FINAL_APP_CODE)"
            FORM_AUTH_PASSED=false
        fi
        
    else
        echo "❌ Form login page test failed!"
        echo "🚫 Expected form login page but got:"
        echo "   HTTP Code: $FORM_HTTP_CODE"
        echo "   Final URL: $FORM_FINAL_URL"
        if [ ${#FORM_CONTENT} -lt 500 ]; then
            echo "   Content preview: $FORM_CONTENT"
        else
            echo "   Content preview: $(echo "$FORM_CONTENT" | head -c 500)..."
        fi
        FORM_TEST_PASSED=false
        FORM_AUTH_PASSED=false
    fi
else
    echo "❌ Initial test failed! HTTP code received: $HTTP_CODE"
    echo "📋 Server logs:"
    cat target/server.log
    FORM_TEST_PASSED=false
    FORM_AUTH_PASSED=false
fi

# Always stop the server
echo "🛑 Stopping server..."
kill $SERVER_PID 2>/dev/null || true

# Wait a moment for graceful shutdown
sleep 2

# Force kill if still running
kill -9 $SERVER_PID 2>/dev/null || true

if [ "$HTTP_CODE" = "200" ] && [ "$FORM_TEST_PASSED" = "true" ] && [ "$FORM_AUTH_PASSED" = "true" ]; then
    echo "🎉 dropwizard-pac4j-demo test completed successfully!"
    echo "✅ All tests passed:"
    echo "   - Application responds with HTTP 200"
    echo "   - Form login page accessible and working"
    echo "   - Form authentication succeeds and demo is reachable"
    exit 0
else
    echo "💥 dropwizard-pac4j-demo test failed!"
    if [ "$HTTP_CODE" != "200" ]; then
        echo "❌ Application HTTP test failed (code: $HTTP_CODE)"
    fi
    if [ "$FORM_TEST_PASSED" != "true" ]; then
        echo "❌ Form login page test failed"
    fi
    if [ "$FORM_AUTH_PASSED" != "true" ]; then
        echo "❌ Form authentication/redirect to demo failed"
    fi
    exit 1
fi
