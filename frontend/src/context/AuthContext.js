import React, { createContext, useState, useEffect } from 'react';
import axios from 'axios';

// Create the authentication context
export const AuthContext = createContext();

/**
 * AuthProvider component that wraps the application and provides authentication state
 * and methods to all child components.
 */
export const AuthProvider = ({ children }) => {
  // State to track if user is authenticated
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  // State to store user data
  const [user, setUser] = useState(null);
  // Loading state for authentication operations
  const [loading, setLoading] = useState(true);

  // Check if user is already logged in on component mount
  useEffect(() => {
    const checkLoggedIn = async () => {
      try {
        // Get user from session storage if available
        const storedUser = sessionStorage.getItem('user');
        
        if (storedUser) {
          const parsedUser = JSON.parse(storedUser);
          setUser(parsedUser);
          setIsAuthenticated(true);
        }
      } catch (error) {
        console.error('Error checking authentication status:', error);
        // Clear any invalid data
        sessionStorage.removeItem('user');
        setUser(null);
        setIsAuthenticated(false);
      } finally {
        setLoading(false);
      }
    };

    checkLoggedIn();
  }, []);

  /**
   * Register a new user
   * @param {Object} userData - User registration data
   * @returns {Promise} - Promise resolving to registration result
   */
  const register = async (userData) => {
    setLoading(true);
    try {
      const response = await axios.post('/api/auth/register', userData);
      setLoading(false);
      return response.data;
    } catch (error) {
      setLoading(false);
      throw error.response?.data || { message: 'Registration failed' };
    }
  };

  /**
   * Log in a user
   * @param {Object} credentials - User login credentials
   * @returns {Promise} - Promise resolving to login result
   */
  const login = async (credentials) => {
    setLoading(true);
    try {
      const response = await axios.post('/api/auth/login', credentials);
      const userData = response.data;
      
      // Store user data in session storage
      sessionStorage.setItem('user', JSON.stringify(userData));
      
      // Update state
      setUser(userData);
      setIsAuthenticated(true);
      setLoading(false);
      
      return userData;
    } catch (error) {
      setLoading(false);
      throw error.response?.data || { message: 'Login failed' };
    }
  };

  /**
   * Log out the current user
   */
  const logout = async () => {
    try {
      // Call logout endpoint
      await axios.post('/api/auth/logout');
    } catch (error) {
      console.error('Error during logout:', error);
    } finally {
      // Clear session storage
      sessionStorage.removeItem('user');
      
      // Update state
      setUser(null);
      setIsAuthenticated(false);
    }
  };

  // Value object to be provided to consumers of this context
  const value = {
    isAuthenticated,
    user,
    loading,
    register,
    login,
    logout
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};