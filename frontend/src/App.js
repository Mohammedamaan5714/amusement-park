import React, { useContext } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import './App.css';
import Chatbot from './components/Chatbot';
import TicketBookingForm from './components/TicketBookingForm';
import ApiTest from './components/ApiTest';
import Login from './components/auth/Login';
import Register from './components/auth/Register';
import ProtectedRoute from './components/auth/ProtectedRoute';
import { AuthProvider, AuthContext } from './context/AuthContext';

function App() {
  return (
    <AuthProvider>
      <Router>
        <AppContent />
      </Router>
    </AuthProvider>
  );
}

function AppContent() {
  const { isAuthenticated, user, logout } = useContext(AuthContext);
  
  return (
    <div className="min-h-screen bg-gray-100">
        <nav className="bg-blue-600 text-white shadow-lg">
          <div className="max-w-6xl mx-auto px-4">
            <div className="flex justify-between">
              <div className="flex space-x-4">
                <div>
                  <Link to="/" className="flex items-center py-5 px-2 text-white hover:text-blue-200">
                    <span className="font-bold text-xl">Amusement Park</span>
                  </Link>
                </div>
                <div className="flex items-center space-x-1">
                  <Link to="/" className="py-5 px-3 text-white hover:text-blue-200">Home</Link>
                  <Link to="/booking" className="py-5 px-3 text-white hover:text-blue-200">Book Tickets</Link>
                  <Link to="/chatbot" className="py-5 px-3 text-white hover:text-blue-200">Chat Assistance</Link>
                  <Link to="/api-test" className="py-5 px-3 text-white hover:text-blue-200">API Test</Link>
                </div>
              </div>
              <div className="flex items-center space-x-4">
                {isAuthenticated ? (
                  <>
                    <span className="text-white">Welcome, {user.username}</span>
                    <button 
                      onClick={logout}
                      className="py-2 px-3 bg-red-500 text-white rounded hover:bg-red-600 transition duration-200"
                    >
                      Logout
                    </button>
                  </>
                ) : (
                  <>
                    <Link to="/login" className="py-2 px-3 bg-green-500 text-white rounded hover:bg-green-600 transition duration-200">Login</Link>
                    <Link to="/register" className="py-2 px-3 bg-blue-400 text-white rounded hover:bg-blue-500 transition duration-200">Register</Link>
                  </>
                )}
              </div>
            </div>
          </div>
        </nav>

        <div className="max-w-6xl mx-auto p-4">
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/" element={
              <div className="text-center py-10">
                <h1 className="text-4xl font-bold text-blue-600 mb-4">Welcome to Our Amusement Park</h1>
                <p className="text-xl mb-8">Experience the thrill and joy of our amazing rides and attractions!</p>
                <div className="flex justify-center space-x-4">
                  <Link to="/booking" className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
                    Book Tickets
                  </Link>
                  <Link to="/chatbot" className="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
                    Chat with Us
                  </Link>
                </div>
              </div>
            } />
            <Route path="/booking" element={<ProtectedRoute element={<TicketBookingForm />} />} />
            <Route path="/chatbot" element={<ProtectedRoute element={<Chatbot />} />} />
            <Route path="/api-test" element={<ProtectedRoute element={<ApiTest />} />} />
          </Routes>
        </div>

        <footer className="bg-blue-600 text-white p-4 mt-8">
          <div className="max-w-6xl mx-auto">
            <div className="flex flex-col md:flex-row justify-between items-center">
              <div className="mb-4 md:mb-0">
                <h3 className="text-xl font-bold">Amusement Park</h3>
                <p>Making memories since 2023</p>
              </div>
              <div>
                <p>© 2023 Amusement Park. All rights reserved.</p>
              </div>
            </div>
          </div>
        </footer>
      </div>
  );
}

export default App;
