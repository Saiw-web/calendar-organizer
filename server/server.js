const express = require('express');
const cors = require('cors');
require('dotenv').config();

const { initDb } = require('./db');
const authRoutes = require('./routes/auth');
const calendarRoutes = require('./routes/calendars');
const planRoutes = require('./routes/plans');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

app.use('/api/auth', authRoutes);
app.use('/api/calendars', calendarRoutes);
app.use('/api/plans', planRoutes);

app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

const start = async () => {
  try {
    await initDb();
    app.listen(PORT, '0.0.0.0', () => {
      console.log(`Server running on port ${PORT}`);
    });
  } catch (err) {
    console.error('Failed to start server:', err);
    process.exit(1);
  }
};

start();
