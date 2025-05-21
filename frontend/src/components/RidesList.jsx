import React, { useState, useEffect } from 'react';
import axios from 'axios';

const RidesList = () => {
  const [ridesByCategory, setRidesByCategory] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // Category icons mapping
  const categoryIcons = {
    'THRILL': '🎢',
    'FAMILY': '👨‍👩‍👧‍👦',
    'KIDS': '👶',
    'THEMED': '🎠'
  };
  
  // Category display names
  const categoryNames = {
    'THRILL': 'Thrill Rides',
    'FAMILY': 'Family-Friendly Rides',
    'KIDS': 'Kids Rides',
    'THEMED': 'Themed Experiences'
  };

  useEffect(() => {
    const fetchRides = async () => {
      try {
        setLoading(true);
        const response = await axios.get('/api/rides/grouped');
        setRidesByCategory(response.data);
        setError('');
      } catch (err) {
        console.error('Error fetching rides:', err);
        setError('Failed to load rides. Please try again later.');
        
        // For demo purposes, set sample data if API fails
        setRidesByCategory({
          'THRILL': [
            { id: '1', name: 'The Thunderbolt', description: 'A high-speed roller coaster with loops, steep drops, and adrenaline-pumping turns.' },
            { id: '2', name: 'Sky Drop', description: 'Experience a sudden vertical drop from great height — not for the faint of heart!' },
            { id: '3', name: 'Vortex Spinner', description: 'A spinning coaster that twists you through a vortex of fun and fear.' },
            { id: '4', name: 'Storm Surge', description: 'A rapid water rafting adventure through wild artificial rapids.' },
            { id: '5', name: 'Fire Loop', description: 'A looping inverted coaster with flame-themed decor and wild inversions.' }
          ],
          'FAMILY': [
            { id: '6', name: 'Fantasy Carousel', description: 'A magical merry-go-round ride with beautifully designed horses and music.' },
            { id: '7', name: 'Jungle Safari Ride', description: 'Ride through an artificial jungle filled with lifelike animal animatronics.' },
            { id: '8', name: 'Adventure Boat', description: 'A relaxing water ride that sails through miniature islands and waterfalls.' },
            { id: '9', name: 'Haunted Mansion', description: 'Explore spooky corridors and ghostly effects in this family-safe scary house.' },
            { id: '10', name: 'Bumper Cars', description: 'Classic fun for everyone — bump into your friends and family!' }
          ],
          'KIDS': [
            { id: '11', name: 'Mini Ferris Wheel', description: 'A kid-sized ferris wheel with a gentle height and colorful lights.' },
            { id: '12', name: 'Buggy Track', description: 'Mini cars on a closed track that kids can drive around freely.' },
            { id: '13', name: 'Tiny Flyers', description: 'Small flying swings perfect for young adventurers who want to soar.' },
            { id: '14', name: 'Magic Train', description: 'A mini train that takes kids through magical tunnels and fairy tale lands.' },
            { id: '15', name: 'Ball Pit Zone', description: 'A huge area filled with soft colorful balls and slides.' }
          ],
          'THEMED': [
            { id: '16', name: 'Park Express Monorail', description: 'Ride above the park with a full scenic view of attractions and zones.' },
            { id: '17', name: 'Sky Gliders', description: 'Soar slowly over the park like a bird with suspended chair lifts.' },
            { id: '18', name: 'Lazy River', description: 'Float along a peaceful water stream in an inflatable tube.' },
            { id: '19', name: '4D Adventure Theater', description: 'Enjoy immersive animated stories with motion chairs and real effects.' },
            { id: '20', name: 'Glow Tunnel Walk', description: 'A walk-through tunnel with glowing lights, mirrors, and illusions.' }
          ]
        });
      } finally {
        setLoading(false);
      }
    };

    fetchRides();
  }, []);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
        {error}
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      <h2 className="text-3xl font-bold text-center mb-8">Our Exciting Rides</h2>
      
      {Object.keys(categoryNames).map(category => (
        <div key={category} className="mb-10">
          <h3 className="text-2xl font-bold mb-4 flex items-center">
            <span className="mr-2">{categoryIcons[category]}</span>
            {categoryNames[category]}
          </h3>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {ridesByCategory[category]?.map(ride => (
              <div key={ride.id} className="border rounded-lg shadow-sm hover:shadow-md transition-shadow p-4">
                <h4 className="text-xl font-semibold mb-2">{ride.name}</h4>
                <p className="text-gray-600">{ride.description}</p>
              </div>
            ))}
          </div>
        </div>
      ))}
      
      <div className="bg-blue-50 p-4 rounded-lg mt-8">
        <h3 className="text-xl font-bold mb-2">Important Information</h3>
        <ul className="list-disc pl-5 space-y-1">
          <li>All rides are subject to height and health restrictions for safety reasons.</li>
          <li>Children below 2.5 ft height can enjoy free entry with Silver, Gold, or Diamond tickets.</li>
          <li>The number of rides you can enjoy depends on your ticket type.</li>
          <li>Silver Ticket: 3 rides | Gold Ticket: 6 rides | Diamond Ticket: 12 rides</li>
        </ul>
      </div>
    </div>
  );
};

export default RidesList;