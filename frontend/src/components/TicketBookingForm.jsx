import React, { useState, useEffect, useContext } from 'react';
import axios from 'axios';
import { AuthContext } from '../context/AuthContext';

const TicketBookingForm = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
  });
  
  // Ticket quantities
  const [ticketQuantities, setTicketQuantities] = useState({
    silver: 0,
    gold: 0,
    diamond: 0
  });
  
  // Get the authenticated user from context
  const { user } = useContext(AuthContext);
  const [ticketTypes, setTicketTypes] = useState([]);
  const [totalPrice, setTotalPrice] = useState(0);
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState('');

  // Fetch available ticket types from backend
  useEffect(() => {
    const fetchTicketTypes = async () => {
      try {
        const response = await axios.get('/api/tickets/types');
        setTicketTypes(response.data);
      } catch (error) {
        console.error('Error fetching ticket types:', error);
        // For demo purposes, set some sample ticket types if API fails
        setTicketTypes([
          { id: 'silver', name: 'Silver', description: 'Amusement park entry fee with 3 rides', rideLimit: 3, price: 299, freeForChildren: true },
          { id: 'gold', name: 'Gold', description: 'Amusement park entry fee with 6 rides', rideLimit: 6, price: 499, freeForChildren: true },
          { id: 'diamond', name: 'Diamond', description: 'Amusement park entry fee with 12 rides', rideLimit: 12, price: 899, freeForChildren: true }
        ]);
      }
    };

    fetchTicketTypes();
  }, []);

  // Calculate total price whenever ticket quantities change
  useEffect(() => {
    let total = 0;
    
    ticketTypes.forEach(ticketType => {
      const quantity = ticketQuantities[ticketType.name.toLowerCase()];
      if (quantity > 0) {
        total += ticketType.price * quantity;
      }
    });
    
    setTotalPrice(total);
  }, [ticketQuantities, ticketTypes]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  const handleQuantityChange = (ticketType, change) => {
    const type = ticketType.toLowerCase();
    const newQuantity = Math.max(0, ticketQuantities[type] + change);
    
    setTicketQuantities({
      ...ticketQuantities,
      [type]: newQuantity
    });
  };

  const getTotalTickets = () => {
    return Object.values(ticketQuantities).reduce((sum, quantity) => sum + quantity, 0);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (getTotalTickets() === 0) {
      setError('Please select at least one ticket');
      return;
    }
    
    setLoading(true);
    setError('');
    
    try {
      // Create ticket object
      const ticketTypeMap = {};
      
      ticketTypes.forEach(ticketType => {
        const quantity = ticketQuantities[ticketType.name.toLowerCase()];
        if (quantity > 0) {
          ticketTypeMap[ticketType.id] = quantity;
        }
      });
      
      const ticketData = {
        userId: user?.id || 'guest',
        userName: formData.name,
        email: formData.email,
        phone: formData.phone,
        ticketTypes: ticketTypeMap,
        totalPrice: totalPrice
      };
      
      // Send booking request to backend
      const response = await axios.post('/api/tickets/book', ticketData);
      
      setSuccess(true);
      // Reset form after successful submission
      setFormData({
        name: '',
        email: '',
        phone: ''
      });
      
      setTicketQuantities({
        silver: 0,
        gold: 0,
        diamond: 0
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
            <h3 className="text-lg font-semibold mb-3">Select Tickets</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {/* Silver Ticket */}
              <div className="border rounded-lg p-4 hover:shadow-md transition-shadow">
                <div className="text-center mb-2">
                  <h4 className="font-bold text-lg">Silver Ticket</h4>
                  <p className="text-sm text-gray-600">and options</p>
                </div>
                <div className="mb-2">
                  <p className="text-sm">Entry + 3 Rides</p>
                  <p className="font-bold">Rs 299</p>
                  <p className="text-xs text-gray-500">Free for children below 2.5ft</p>
                </div>
                <div className="flex justify-center items-center mt-3">
                  <button 
                    type="button" 
                    onClick={() => handleQuantityChange('silver', -1)}
                    className="bg-gray-200 px-3 py-1 rounded-l"
                  >
                    -
                  </button>
                  <span className="px-4 py-1 border-t border-b">{ticketQuantities.silver}</span>
                  <button 
                    type="button" 
                    onClick={() => handleQuantityChange('silver', 1)}
                    className="bg-gray-200 px-3 py-1 rounded-r"
                  >
                    +
                  </button>
                </div>
              </div>
              
              {/* Gold Ticket */}
              <div className="border rounded-lg p-4 hover:shadow-md transition-shadow">
                <div className="text-center mb-2">
                  <h4 className="font-bold text-lg">Gold Ticket</h4>
                </div>
                <div className="mb-2">
                  <p className="text-sm">Entry + 6 Rides</p>
                  <p className="font-bold">Rs 499</p>
                  <p className="text-xs text-gray-500">Free for children below 2.5ft</p>
                </div>
                <div className="flex justify-center items-center mt-3">
                  <button 
                    type="button" 
                    onClick={() => handleQuantityChange('gold', -1)}
                    className="bg-gray-200 px-3 py-1 rounded-l"
                  >
                    -
                  </button>
                  <span className="px-4 py-1 border-t border-b">{ticketQuantities.gold}</span>
                  <button 
                    type="button" 
                    onClick={() => handleQuantityChange('gold', 1)}
                    className="bg-gray-200 px-3 py-1 rounded-r"
                  >
                    +
                  </button>
                </div>
              </div>
              
              {/* Diamond Ticket */}
              <div className="border rounded-lg p-4 hover:shadow-md transition-shadow">
                <div className="text-center mb-2">
                  <h4 className="font-bold text-lg">Diamond Ticket</h4>
                </div>
                <div className="mb-2">
                  <p className="text-sm">Entry + 12 Rides</p>
                  <p className="font-bold">Rs 899</p>
                  <p className="text-xs text-gray-500">Free for children below 2.5ft</p>
                </div>
                <div className="flex justify-center items-center mt-3">
                  <button 
                    type="button" 
                    onClick={() => handleQuantityChange('diamond', -1)}
                    className="bg-gray-200 px-3 py-1 rounded-l"
                  >
                    -
                  </button>
                  <span className="px-4 py-1 border-t border-b">{ticketQuantities.diamond}</span>
                  <button 
                    type="button" 
                    onClick={() => handleQuantityChange('diamond', 1)}
                    className="bg-gray-200 px-3 py-1 rounded-r"
                  >
                    +
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <div className="bg-gray-100 p-4 rounded-lg">
            <div className="flex justify-between items-center">
              <div>
                <p className="text-sm">2*Diamond: {ticketQuantities.diamond * 2}</p>
                <p className="text-sm">2*Gold: {ticketQuantities.gold * 2}</p>
              </div>
              <button 
                type="submit" 
                className="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50"
                disabled={loading || getTotalTickets() === 0}
              >
                {loading ? 'Processing...' : 'Purchase'}
              </button>
            </div>
            <div className="mt-2">
              <p className="font-bold text-lg">Total: Rs {totalPrice.toFixed(2)}</p>
            </div>
          </div>
        </form>
      )}
    </div>
  );
};

export default TicketBookingForm;