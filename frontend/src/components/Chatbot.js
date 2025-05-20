import React, { useState, useContext } from 'react';
import axios from 'axios';
import { AuthContext } from '../context/AuthContext';

const Chatbot = () => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  
  // Get the authenticated user from context
  const { user } = useContext(AuthContext);

  const handleInputChange = (e) => {
    setInput(e.target.value);
  };

  const handleSendMessage = async () => {
    if (input.trim() === '') return;

    // Add user message to chat
    const userMessage = { text: input, sender: 'user' };
    setMessages([...messages, userMessage]);
    setInput('');

    try {
      // Send message to backend with the authenticated user's ID
      const response = await axios.post('/api/chat', { userId: user.id, message: input });
      // Note: Using user.id assuming the backend returns user object with id field
      // If the field is different (e.g., userId, _id), adjust accordingly
      
      // Add bot response to chat
      const botMessage = { text: response.data.message, sender: 'bot' };
      setMessages(prevMessages => [...prevMessages, botMessage]);
    } catch (error) {
      console.error('Error sending message:', error);
      // Add error message
      const errorMessage = { text: 'Sorry, I encountered an error. Please try again.', sender: 'bot' };
      setMessages(prevMessages => [...prevMessages, errorMessage]);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSendMessage();
    }
  };

  return (
    <div className="flex flex-col h-screen bg-gray-100 p-4">
      <div className="bg-white rounded-lg shadow-md p-4 flex-grow overflow-auto mb-4">
        <div className="flex flex-col space-y-4">
          {messages.length === 0 ? (
            <div className="text-center text-gray-500 my-8">
              <p>Welcome to Amusement Park Assistant!</p>
              <p>Ask me about rides, tickets, or anything about the park.</p>
            </div>
          ) : (
            messages.map((message, index) => (
              <div
                key={index}
                className={`flex ${message.sender === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                <div
                  className={`max-w-xs lg:max-w-md px-4 py-2 rounded-lg ${message.sender === 'user' ? 'bg-blue-500 text-white' : 'bg-gray-200 text-gray-800'}`}
                >
                  {message.text}
                </div>
              </div>
            ))
          )}
        </div>
      </div>

      <div className="flex items-center bg-white rounded-lg shadow-md p-2">
        <input
          type="text"
          value={input}
          onChange={handleInputChange}
          onKeyPress={handleKeyPress}
          placeholder="Ask about rides, tickets, or assistance..."
          className="flex-grow px-4 py-2 focus:outline-none"
        />
        <button
          onClick={handleSendMessage}
          className="ml-2 bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition duration-200"
        >
          Send
        </button>
      </div>
    </div>
  );
};

export default Chatbot;