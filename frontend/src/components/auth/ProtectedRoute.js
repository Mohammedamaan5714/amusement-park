import React, { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';

/**
 * ProtectedRoute component to restrict access to authenticated users only
 * @param {Object} props - Component props
 * @param {React.Component} props.element - The component to render if authenticated
 */
const ProtectedRoute = ({ element }) => {
  // Get authentication context
  const { isAuthenticated, loading } = useContext(AuthContext);
  
  // Show loading state while checking authentication
  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }
  
  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  
  // Render the protected component if authenticated
  return element;
};

export default ProtectedRoute;