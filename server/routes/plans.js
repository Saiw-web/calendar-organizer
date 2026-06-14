const express = require('express');
const { pool } = require('../db');
const auth = require('../middleware/auth');
const { v4: uuidv4 } = require('uuid');

const router = express.Router();

router.get('/', auth, async (req, res) => {
  try {
    const result = await pool.query(
      'SELECT * FROM plans WHERE user_email = $1 ORDER BY due_date ASC NULLS LAST, created_at DESC',
      [req.user.email]
    );
    res.json(result.rows.map(formatPlan));
  } catch (err) {
    console.error('Get plans error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

router.post('/', auth, async (req, res) => {
  try {
    const { title, description, due_date, priority, status } = req.body;
    if (!title) {
      return res.status(400).json({ error: 'Bad request', message: 'Title is required' });
    }

    const id = uuidv4();
    await pool.query(
      `INSERT INTO plans (id, title, description, due_date, priority, status, user_email)
       VALUES ($1, $2, $3, $4, $5, $6, $7)`,
      [id, title, description || null, due_date || null, priority || 'medium', status || 'active', req.user.email]
    );

    const result = await pool.query('SELECT * FROM plans WHERE id = $1', [id]);
    res.status(201).json(formatPlan(result.rows[0]));
  } catch (err) {
    console.error('Create plan error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

router.put('/:planId', auth, async (req, res) => {
  try {
    const { planId } = req.params;
    const { title, description, due_date, priority, status } = req.body;

    const result = await pool.query(
      `UPDATE plans SET
       title = COALESCE($1, title),
       description = $2,
       due_date = $3,
       priority = COALESCE($4, priority),
       status = COALESCE($5, status),
       updated_at = NOW()
       WHERE id = $6 AND user_email = $7 RETURNING *`,
      [title, description, due_date || null, priority, status, planId, req.user.email]
    );

    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Not found', message: 'Plan not found' });
    }
    res.json(formatPlan(result.rows[0]));
  } catch (err) {
    console.error('Update plan error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

router.delete('/:planId', auth, async (req, res) => {
  try {
    const { planId } = req.params;
    const result = await pool.query(
      'DELETE FROM plans WHERE id = $1 AND user_email = $2 RETURNING id',
      [planId, req.user.email]
    );
    if (result.rows.length === 0) {
      return res.status(404).json({ error: 'Not found', message: 'Plan not found' });
    }
    res.status(204).send();
  } catch (err) {
    console.error('Delete plan error:', err);
    res.status(500).json({ error: 'Server error', message: 'Internal server error' });
  }
});

function formatPlan(row) {
  return {
    id: row.id,
    title: row.title,
    description: row.description,
    dueDate: row.due_date,
    priority: row.priority,
    status: row.status,
    createdAt: row.created_at,
    updatedAt: row.updated_at,
  };
}

module.exports = router;
