import { useState, useEffect } from 'react';
import { leavesApi } from '../api/client';

const STATUS_COLORS = {
  PENDING:  { bg: '#fff8e1', color: '#b8860b', border: '#ffe082' },
  APPROVED: { bg: '#e8f5e9', color: '#2e7d32', border: '#a5d6a7' },
  REJECTED: { bg: '#ffebee', color: '#c62828', border: '#ef9a9a' },
};

function LeaveList() {
  const [leaves, setLeaves] = useState([]);
  const [filter, setFilter] = useState({ memberId: '', status: '' });
  const [loading, setLoading] = useState(true);

  const fetchLeaves = async () => {
    setLoading(true);
    try {
      const params = {};
      if (filter.memberId) params.memberId = filter.memberId;
      if (filter.status) params.status = filter.status;
      const res = await leavesApi.getAll(params);
      setLeaves(res.data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchLeaves(); }, [filter]);

  const handleStatusChange = async (id, status) => {
    try {
      await leavesApi.updateStatus(id, status);
      fetchLeaves();
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to update status');
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this leave request?')) return;
    await leavesApi.delete(id);
    fetchLeaves();
  };

  return (
    <div>
      {/* Filters */}
      <div style={{
        background: '#fff',
        borderRadius: '10px',
        padding: '16px 20px',
        marginBottom: '16px',
        boxShadow: '0 1px 4px rgba(0,0,0,0.08)',
        display: 'flex',
        gap: '12px',
        alignItems: 'center',
        flexWrap: 'wrap',
      }}>
        <span style={{ fontSize: '13px', fontWeight: '600', color: '#555' }}>Filter:</span>
        <select value={filter.status} onChange={e => setFilter(p => ({ ...p, status: e.target.value }))}
          style={{ padding: '7px 12px', borderRadius: '6px', border: '1px solid #ddd', fontSize: '13px' }}>
          <option value="">All Statuses</option>
          <option value="PENDING">Pending</option>
          <option value="APPROVED">Approved</option>
          <option value="REJECTED">Rejected</option>
        </select>
        {filter.status && (
          <button onClick={() => setFilter({ memberId: '', status: '' })}
            style={{ padding: '7px 12px', borderRadius: '6px', border: '1px solid #ddd',
              background: '#f5f5f5', fontSize: '13px' }}>
            Clear
          </button>
        )}
        <span style={{ marginLeft: 'auto', fontSize: '13px', color: '#888' }}>
          {leaves.length} request{leaves.length !== 1 ? 's' : ''}
        </span>
      </div>

      {/* List */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>Loading...</div>
      ) : leaves.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '40px', color: '#888',
          background: '#fff', borderRadius: '10px' }}>
          No leave requests found.
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
          {leaves.map(leave => {
            const sc = STATUS_COLORS[leave.status];
            return (
              <div key={leave.id} style={{
                background: '#fff',
                borderRadius: '10px',
                padding: '18px 20px',
                boxShadow: '0 1px 4px rgba(0,0,0,0.08)',
                display: 'flex',
                alignItems: 'center',
                gap: '16px',
                flexWrap: 'wrap',
              }}>
                <div style={{ flex: 1, minWidth: '200px' }}>
                  <div style={{ fontWeight: '600', fontSize: '15px', marginBottom: '4px' }}>
                    {leave.teamMember.name}
                  </div>
                  <div style={{ fontSize: '13px', color: '#666' }}>
                    {leave.startDate} → {leave.endDate}
                  </div>
                  <div style={{ fontSize: '13px', color: '#888', marginTop: '2px' }}>
                    {leave.reason}
                  </div>
                </div>

                <span style={{
                  padding: '4px 12px',
                  borderRadius: '20px',
                  fontSize: '12px',
                  fontWeight: '600',
                  background: sc.bg,
                  color: sc.color,
                  border: `1px solid ${sc.border}`,
                }}>
                  {leave.status}
                </span>

                <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
                  {leave.status === 'PENDING' && (
                    <>
                      <button onClick={() => handleStatusChange(leave.id, 'APPROVED')}
                        style={{ padding: '6px 14px', borderRadius: '6px', border: 'none',
                          background: '#2e7d32', color: '#fff', fontSize: '12px', fontWeight: '600' }}>
                        Approve
                      </button>
                      <button onClick={() => handleStatusChange(leave.id, 'REJECTED')}
                        style={{ padding: '6px 14px', borderRadius: '6px', border: 'none',
                          background: '#c62828', color: '#fff', fontSize: '12px', fontWeight: '600' }}>
                        Reject
                      </button>
                    </>
                  )}
                  <button onClick={() => handleDelete(leave.id)}
                    style={{ padding: '6px 14px', borderRadius: '6px',
                      border: '1px solid #ddd', background: '#f5f5f5',
                      fontSize: '12px', color: '#666' }}>
                    Delete
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

export default LeaveList;
