import React, { useState, useEffect } from 'react';
import axios from 'axios';

/**
 * Component to test API integration between React frontend and Spring Boot backend
 */
const ApiTest = () => {
  const [pingResult, setPingResult] = useState('');
  const [echoMessage, setEchoMessage] = useState('');
  const [echoResult, setEchoResult] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Test ping endpoint on component mount
  useEffect(() => {
    testPingEndpoint();
  }, []);

  // Test the GET /api/test/ping endpoint
  const testPingEndpoint = async () => {
    setLoading(true);
    setError('');
    try {
      const response = await axios.get('/api/test/ping');
      setPingResult(response.data.message);
    } catch (err) {
      console.error('Ping test failed:', err);
      setError(`Ping test failed: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  // Test the POST /api/test/echo endpoint
  const testEchoEndpoint = async () => {
    if (!echoMessage.trim()) return;
    
    setLoading(true);
    setError('');
    try {
      const response = await axios.post('/api/test/echo', { message: echoMessage });
      setEchoResult(response.data.message);
    } catch (err) {
      console.error('Echo test failed:', err);
      setError(`Echo test failed: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto bg-white rounded-xl shadow-md overflow-hidden md:max-w-2xl m-4 p-6">
      <h2 className="text-2xl font-bold text-center text-blue-600 mb-6">API Integration Test</h2>
      
      {/* Ping Test Section */}
      <div className="mb-6 p-4 border rounded-lg">
        <h3 className="text-lg font-semibold mb-2">GET Test (Ping)</h3>
        <button 
          onClick={testPingEndpoint}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mb-4"
          disabled={loading}
        >
          Test Ping Endpoint
        </button>
        {pingResult && (
          <div className="mt-2 p-3 bg-green-100 rounded">
            <p><strong>Response:</strong> {pingResult}</p>
          </div>
        )}
      </div>

      {/* Echo Test Section */}
      <div className="mb-6 p-4 border rounded-lg">
        <h3 className="text-lg font-semibold mb-2">POST Test (Echo)</h3>
        <div className="mb-4">
          <input
            type="text"
            value={echoMessage}
            onChange={(e) => setEchoMessage(e.target.value)}
            placeholder="Enter a message to echo"
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
          />
        </div>
        <button 
          onClick={testEchoEndpoint}
          className="bg-green-500 hover:bg-green-700 text-white font-bold py-2 px-4 rounded mb-4"
          disabled={loading || !echoMessage.trim()}
        >
          Test Echo Endpoint
        </button>
        {echoResult && (
          <div className="mt-2 p-3 bg-green-100 rounded">
            <p><strong>Response:</strong> {echoResult}</p>
          </div>
        )}
      </div>

      {/* Error Display */}
      {error && (
        <div className="p-3 bg-red-100 text-red-700 rounded">
          <p>{error}</p>
          <div className="mt-2">
            <p className="text-sm">Troubleshooting tips:</p>
            <ul className="list-disc pl-5 text-sm">
              <li>Make sure the backend server is running on port 8080</li>
              <li>Check that CORS is properly configured in WebConfig.java</li>
              <li>Verify the proxy setting in package.json is correct</li>
            </ul>
          </div>
        </div>
      )}

      {/* Integration Guide Link */}
      <div className="mt-6 text-center">
        <p className="text-sm text-gray-600">
          For detailed integration instructions, see the 
          <a href="#" className="text-blue-500 hover:text-blue-700 ml-1">
            Integration Guide
          </a>
        </p>
      </div>
    </div>
  );
};

export default ApiTest;