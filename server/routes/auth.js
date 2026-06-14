const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { pool } = require('../db');
const auth = require('../middleware/auth');
require('dotenv').config();

const router = express.Router();

router.post('/register', async (req, res) => {
  try {
    const { name, email, password } = req.body;
    if (!name || !email || !password) {
      return res.status(400).json({ error: 'Bad request', message: 'Name, email and password are required' });
    }

    const existing = await pool.query('SELECT id FROM users WHERE email = $1', [email]);
    if (existing.rows.length > 0) {
      return res.status(409).json({ error: 'Conflict', message: 'Email already registered' });
    }

    const hashedPassword = await bcrypt.hash(password, 12);
    const result = await pool.query(
      'INSERT INTO users (name, email, password) VALUES ($1, $2, $3) RETURNING id, name, email, avatar_url',
      [name, email, hashedPassword]
    );
    const user = result.rows[0];

    const token = jwt.sign(
      { id: user.id, email: user.email, name: user.name },
      process.env.JWT_SECRET,
      { expiresIn: '30d' }
    );

    res.status(201).json({ token, user });
  } catch (err) {
    console.error('Register error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

router.post('/login', async (req, res) => {
  try {
    const { email, password } = req.body;
    if (!email || !password) {
      return res.status(400).json({ error: 'Bad request', message: 'Email and password are required' });
    }

    const result = await pool.query('SELECT id, name, email, password, avatar_url FROM users WHERE email = $1', [email]);
    if (result.rows.length === 0) {
      return res.status(401).json({ error: 'Unauthorized', message: 'Invalid email or password' });
    }

    const user = result.rows[0];
    const validPassword = await bcrypt.compare(password, user.password);
    if (!validPassword) {
      return res.status(401).json({ error: 'Unauthorized', message: 'Invalid email or password' });
    }

    const token = jwt.sign(
      { id: user.id, email: user.email, name: user.name },
      process.env.JWT_SECRET,
      { expiresIn: '30d' }
    );

    res.json({
      token,
      user: {
        id: user.id,
        name: user.name,
        email: user.email,
        avatarUrl: user.avatar_url,
      }
    });
  } catch (err) {
    console.error('Login error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

router.get('/me', auth, async (req, res) => {
  try {
    const result = await pool.query(
      'SELECT id, name, email, avatar_url FROM users WHERE email = $1',
      [req.user.email]
    );
    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Not found', message: 'User not found' });
    }
    const user = result.rows[0];
    res.json({
      id: user.id,
      name: user.name,
      email: user.email,
      avatarUrl: user.avatar_url,
    });
  } catch (err) {
    console.error('Get profile error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

module.exports = router;
