import React, { useState, useEffect, useContext } from 'react';
import axios from 'axios';
import { AuthContext } from '../context/AuthContext';

const TicketBookingForm = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    selectedRides: [],
  });
  
  // Get the authenticated user from context
  const { user } = useContext(AuthContext);
  const [rides, setRides] = useState([]);
  const [entryFee, setEntryFee] = useState(25.00); // Default entry fee
  const [totalPrice, setTotalPrice] = useState(entryFee);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  // Fetch available rides from backend
  useEffect(() => {
    const fetchRides = async () => {
      try {
        const response = await axios.get('/api/rides');
        setRides(response.data);
      } catch (error) {
        console.error('Error fetching rides:', error);
        // For demo purposes, set some sample rides if API fails
        setRides([
          { id: '1', name: 'Roller Coaster', description: 'Thrilling high-speed ride', price: 10.00 },
          { id: '2', name: 'Ferris Wheel', description: 'Scenic view of the park', price: 8.00 },
          { id: '3', name: 'Water Slide', description: 'Cool water adventure', price: 12.00 },
          { id: '4', name: 'Bumper Cars', description: 'Fun driving experience', price: 7.00 },
          { id: '5', name: 'Haunted House', description: 'Spooky adventure', price: 9.00 }
        ]);
      }
    };

    fetchRides();
  }, []);

  // Calculate total price whenever selected rides change
  useEffect(() => {
    const ridesTotal = formData.selectedRides.reduce((total, rideId) => {
      const ride = rides.find(r => r.id === rideId);
      return total + (ride ? ride.price : 0);
    }, 0);
    
    setTotalPrice(entryFee + ridesTotal);
  }, [formData.selectedRides, rides, entryFee]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleRideSelection = (e) => {
    const { value, checked } = e.target;
    
    if (checked) {
      setFormData({
        ...formData,
        selectedRides: [...formData.selectedRides, value]
      });
    } else {
      setFormData({
        ...formData,
        selectedRides: formData.selectedRides.filter(id => id !== value)
      });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    
    try {
      // Create ticket object
      const ticketData = {
        userId: user.id, // Using the authenticated user's ID
        rideIds: formData.selectedRides,
        foodItems: [], // Not including food items as per requirements
        entryFee: entryFee,
        totalPrice: totalPrice,
        userDetails: {
          name: formData.name,
          email: formData.email,
          phone: formData.phone
        }
      };
      
      // Send booking request to backend
      const response = await axios.post('/api/book', ticketData);
      
      setSuccess(true);
      // Reset form after successful submission
      setFormData({
        name: '',
        email: '',
        phone: '',
        selectedRides: []
      });
    } catch (error) {
      console.error('Error booking ticket:', error);
      setError('Failed to book ticket. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-6 bg-white rounded-lg shadow-md">
      <h2 className="text-2xl font-bold text-center mb-6">Book Your Amusement Park Tickets</h2>
      
      {success ? (
        <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
          <p>Ticket booked successfully! Check your email for confirmation.</p>
          <button 
            onClick={() => setSuccess(false)}
            className="mt-2 bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
          >
            Book Another Ticket
          </button>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="space-y-6">
          {error && (
            <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
              {error}
            </div>
          )}
          
          <div className="bg-blue-50 p-4 rounded-lg mb-6">
            <h3 className="text-lg font-semibold mb-2">Pricing Information</h3>
            <p className="text-gray-700">Entry Fee: ${entryFee.toFixed(2)}</p>
            <p className="text-gray-700">Selected Rides: ${(totalPrice - entryFee).toFixed(2)}</p>
            <p className="text-xl font-bold text-blue-800 mt-2">Total: ${totalPrice.toFixed(2)}</p>
          </div>
          
          <div className="space-y-4">
            <h3 className="text-lg font-semibold">Personal Information</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-gray-700 mb-1">Full Name</label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleInputChange}
                  required
                  className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-gray-700 mb-1">Email</label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleInputChange}
                  required
                  className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
              <div>
                <label className="block text-gray-700 mb-1">Phone Number</label>
                <input
                  type="tel"
                  name="phone"
                  value={formData.phone}
                  onChange={handleInputChange}
                  required
                  className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>
          </div>
          
          <div>
            <h3 className="text-lg font-semibold mb-3">Select Rides</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {rides.map(ride => (
                <div key={ride.id} className="border rounded-lg p-4 hover:bg-gray-50">
                  <label className="flex items-start space-x-3 cursor-pointer">
                    <input
                      type="checkbox"
                      value={ride.id}
                      checked={formData.selectedRides.includes(ride.id)}
                      onChange={handleRideSelection}
                      className="mt-1"
                    />
                    <div>
                      <p className="font-medium">{ride.name}</p>
                      <p className="text-sm text-gray-600">{ride.description}</p>
                      <p className="text-blue-600 font-medium">${ride.price.toFixed(2)}</p>
                    </div>
                  </label>
                </div>
              ))}
            </div>
          </div>
          
          <div className="flex justify-center mt-6">
            <button
              type="submit"
              disabled={loading}
              className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition duration-200 disabled:bg-blue-300"
            >
              {loading ? 'Processing...' : 'Book Ticket'}
            </button>
          </div>
        </form>
      )}
      
      <div className="mt-8 p-4 bg-yellow-50 rounded-lg">
        <h3 className="text-lg font-semibold mb-2">Food Stalls Information</h3>
        <p className="text-gray-700">The park features various food stalls offering a wide range of cuisines. Food items can be purchased directly at the stalls inside the park.</p>
        <p className="text-gray-700 mt-2">Options include:</p>
        <ul className="list-disc pl-5 mt-1 text-gray-700">
          <li>Fast Food Corner - Burgers, Fries, Hot Dogs</li>
          <li>Sweet Treats - Ice Cream, Cotton Candy, Funnel Cakes</li>
          <li>Healthy Options - Salads, Wraps, Fresh Juices</li>
          <li>International Cuisine - Pizza, Tacos, Sushi</li>
        </ul>
      </div>
    </div>
  );
};

export default TicketBookingForm;