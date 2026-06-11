import { useState, useEffect } from 'react';
import { membersApi, leavesApi } from '../api/client';

function LeaveForm({ onCreated }) {
  const [members, setMembers] = useState([]);
  const [form, setForm] = useState({
    teamMemberId: '',
    startDate: '',
    endDate: '',
    reason: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    membersApi.getAll().then(res => setMembers(res.data));
  }, []);

  const handleChange = (e) => {
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await leavesApi.create({
        ...form,
        teamMemberId: parseInt(form.teamMemberId),
      });
      onCreated();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to create leave request');
    } finally {
      setLoading(false);
    }
  };

  const inputStyle = {
    width: '100%',
    padding: '10px 12px',
    borderRadius: '6px',
    border: '1px solid #ddd',
    fontSize: '14px',
    marginTop: '6px',
  };

  const labelStyle = {
    display: 'block',
    fontSize: '13px',
    fontWeight: '600',
    color: '#555',
  };

  return (
    <div style={{
      background: '#fff',
      borderRadius: '10px',
      padding: '28px',
      boxShadow: '0 1px 4px rgba(0,0,0,0.08)',
      maxWidth: '520px',
    }}>
      <h2 style={{ marginBottom: '20px', fontSize: '18px', color: '#1a2c4e' }}>
        New Leave Request
      </h2>

      {error && (
        <div style={{
          background: '#fff0f0',
          border: '1px solid #ffcccc',
          color: '#cc0000',
          padding: '10px 14px',
          borderRadius: '6px',
          marginBottom: '16px',
          fontSize: '14px',
        }}>
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '16px' }}>
          <label style={labelStyle}>Team Member</label>
          <select name="teamMemberId" value={form.teamMemberId}
            onChange={handleChange} required style={inputStyle}>
            <option value="">Select a member...</option>
            {members.map(m => (
              <option key={m.id} value={m.id}>{m.name}</option>
            ))}
          </select>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '16px' }}>
          <div>
            <label style={labelStyle}>Start Date</label>
            <input type="date" name="startDate" value={form.startDate}
              onChange={handleChange} required style={inputStyle} />
          </div>
          <div>
            <label style={labelStyle}>End Date</label>
            <input type="date" name="endDate" value={form.endDate}
              onChange={handleChange} required style={inputStyle} />
          </div>
        </div>

        <div style={{ marginBottom: '20px' }}>
          <label style={labelStyle}>Reason</label>
          <textarea name="reason" value={form.reason} onChange={handleChange}
            required rows={3} style={{ ...inputStyle, resize: 'vertical' }}
            placeholder="Brief reason for leave..." />
        </div>

        <button type="submit" disabled={loading} style={{
          background: '#1a2c4e',
          color: '#fff',
          padding: '11px 24px',
          borderRadius: '6px',
          border: 'none',
          fontSize: '14px',
          fontWeight: '600',
          opacity: loading ? 0.7 : 1,
        }}>
          {loading ? 'Submitting...' : 'Submit Request'}
        </button>
      </form>
    </div>
  );
}

export default LeaveForm;
