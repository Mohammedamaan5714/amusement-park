# Spring Boot & React Integration Guide

This guide will help you run and integrate your Spring Boot backend with your React frontend, including CORS configuration, proxy setup, and testing API communication.

## 1. Running the Backend (Spring Boot)

1. Navigate to the backend directory:
   ```
   cd e:\amusementpark\backend
   ```

2. Start the Spring Boot application:
   ```
   ./mvnw spring-boot:run
   ```
   (On Windows, use `mvnw.cmd` instead of `./mvnw`)

3. The backend will start on `http://localhost:8080`

## 2. CORS Configuration (Backend)

We've already created a CORS configuration in `WebConfig.java` that allows requests from the React frontend:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000") // React frontend URL
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // 1 hour max age
    }
}
```

## 3. Running the Frontend (React)

1. Navigate to the frontend directory:
   ```
   cd e:\amusementpark\frontend
   ```

2. Install dependencies (if not already done):
   ```
   npm install
   ```

3. Start the React development server:
   ```
   npm start
   ```

4. The frontend will start on `http://localhost:3000`

## 4. Proxy Configuration (Frontend)

Your React app's `package.json` already includes a proxy configuration:

```json
"proxy": "http://localhost:8080"
```

This configuration allows you to make API calls without specifying the full URL. For example, instead of:
```javascript
axios.post('http://localhost:8080/api/chat', { message: input })
```

You can simply use:
```javascript
axios.post('/api/chat', { message: input })
```

## 5. Testing API Communication

1. Start both the backend and frontend servers as described above.

2. Navigate to the Chatbot page in your React app (http://localhost:3000/chatbot).

3. Type a message in the chat input and press Enter or click the send button.

4. The React app will send the message to the backend API endpoint `/api/chat`.

5. If everything is configured correctly, you should receive a response from the backend.

## 6. Troubleshooting Common Issues

### CORS Errors

If you see errors like "Access to XMLHttpRequest has been blocked by CORS policy" in the browser console:

1. Verify that the WebConfig.java file is properly configured.
2. Make sure the allowed origin matches your frontend URL exactly (including http/https).
3. Restart the Spring Boot application after making changes.

### Proxy Not Working

If API calls are not being proxied correctly:

1. Ensure the "proxy" field in package.json is set correctly.
2. Restart the React development server after making changes to package.json.
3. Make sure you're using relative URLs in your API calls (e.g., '/api/chat' not 'http://localhost:8080/api/chat').

### Network Errors

If you see network errors in the console:

1. Verify both servers are running (backend on port 8080, frontend on port 3000).
2. Check that your API endpoints match the ones expected by the frontend.
3. Look at the Spring Boot console for any backend errors.

### React App Test Failures

The current App.test.js is looking for "learn react" text which doesn't exist in your current App component. Update the test or skip it if needed.

## 7. Next Steps

- Add authentication to secure your API endpoints
- Implement error handling and loading states in the frontend
- Add more features to your chat functionality
- Deploy your application to a production environment