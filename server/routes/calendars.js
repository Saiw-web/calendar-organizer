const express = require('express');
const { pool } = require('../db');
const auth = require('../middleware/auth');
const { v4: uuidv4 } = require('uuid');

const router = express.Router();

router.get('/', auth, async (req, res) => {
  try {
    const result = await pool.query(
      `SELECT c.* FROM calendars c
       JOIN calendar_members cm ON c.id = cm.calendar_id
       WHERE cm.user_email = $1
       UNION
       SELECT * FROM calendars WHERE owner_email = $1`,
      [req.user.email]
    );
    res.json(result.rows.map(formatCalendar));
  } catch (err) {
    console.error('Get calendars error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

router.post('/', auth, async (req, res) => {
  try {
    const { name, description, color } = req.body;
    if (!name) {
      return res.status(400).json({ error: 'Bad request', message: 'Name is required' });
    }

    const id = uuidv4();
    await pool.query(
      'INSERT INTO calendars (id, name, description, color, owner_email) VALUES ($1, $2, $3, $4, $5)',
      [id, name, description || null, color || null, req.user.email]
    );

    await pool.query(
      'INSERT INTO calendar_members (calendar_id, user_email, role) VALUES ($1, $2, $3)',
      [id, req.user.email, 'owner']
    );

    const result = await pool.query('SELECT * FROM calendars WHERE id = $1', [id]);
    res.status(201).json(formatCalendar(result.rows[0]));
  } catch (err) {
    console.error('Create calendar error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

router.post('/join', auth, async (req, res) => {
  try {
    const { code } = req.body;
    if (!code) {
      return res.status(400).json({ error: 'Bad request', message: 'Invitation code is required' });
    }

    const calendarResult = await pool.query('SELECT id, name FROM calendars WHERE id = $1', [code]);
    if (calendarResult.rows.length === 0) {
      return res.status(404).json({ error: 'Not found', message: 'Calendar not found' });
    }

    const calendarId = calendarResult.rows[0].id;
    const existing = await pool.query(
      'SELECT id FROM calendar_members WHERE calendar_id = $1 AND user_email = $2',
      [calendarId, req.user.email]
    );

    if (existing.rows.length === 0) {
      await pool.query(
        'INSERT INTO calendar_members (calendar_id, user_email) VALUES ($1, $2)',
        [calendarId, req.user.email]
      );
    }

    res.json({ calendarId, message: `Joined ${calendarResult.rows[0].name}` });
  } catch (err) {
    console.error('Join calendar error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

router.get('/:calendarId/events', auth, async (req, res) => {
  try {
    const { calendarId } = req.params;
    const { date } = req.query;

    let query = 'SELECT * FROM events WHERE calendar_id = $1';
    const params = [calendarId];

    if (date) {
      query += ' AND DATE(start_time) = $2';
      params.push(date);
    }

    query += ' ORDER BY start_time ASC';
    const result = await pool.query(query, params);
    res.json(result.rows.map(formatEvent));
  } catch (err) {
    console.error('Get events error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

router.post('/:calendarId/events', auth, async (req, res) => {
  try {
    const { calendarId } = req.params;
    const { title, description, start_time, end_time, location, color } = req.body;

    if (!title || !start_time || !end_time) {
      return res.status(400).json({ error: 'Bad request', message: 'Title, start_time and end_time are required' });
    }

    const id = uuidv4();
    await pool.query(
      `INSERT INTO events (id, calendar_id, title, description, start_time, end_time, location, color, created_by)
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)`,
      [id, calendarId, title, description || null, start_time, end_time, location || null, color || null, req.user.email]
    );

    const result = await pool.query('SELECT * FROM events WHERE id = $1', [id]);
    res.status(201).json(formatEvent(result.rows[0]));
  } catch (err) {
    console.error('Create event error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

router.put('/:calendarId/events/:eventId', auth, async (req, res) => {
  try {
    const { eventId } = req.params;
    const { title, description, start_time, end_time, location, color } = req.body;

    const result = await pool.query(
      `UPDATE events SET title = COALESCE($1, title), description = $2,
       start_time = COALESCE($3, start_time), end_time = COALESCE($4, end_time),
       location = $5, color = $6, updated_at = NOW()
       WHERE id = $7 RETURNING *`,
      [title, description, start_time, end_time, location, color, eventId]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Not found', message: 'Event not found' });
    }
    res.json(formatEvent(result.rows[0]));
  } catch (err) {
    console.error('Update event error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

router.delete('/:calendarId/events/:eventId', auth, async (req, res) => {
  try {
    const { eventId } = req.params;
    const result = await pool.query('DELETE FROM events WHERE id = $1 RETURNING id', [eventId]);
    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Not found', message: 'Event not found' });
    }
    res.status(204).send();
  } catch (err) {
    console.error('Delete event error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

function formatCalendar(row) {
  return {
    id: row.id,
    name: row.name,
    ownerEmail: row.owner_email,
    description: row.description,
    color: row.color,
  };
}

function formatEvent(row) {
  return {
    id: row.id,
    calendarId: row.calendar_id,
    title: row.title,
    description: row.description,
    startTime: row.start_time,
    endTime: row.end_time,
    location: row.location,
    color: row.color,
    createdAt: row.created_at,
    updatedAt: row.updated_at,
  };
}

module.exports = router;
