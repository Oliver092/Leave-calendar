import { useState, useEffect } from 'react';
import { onCallApi } from '../api/client';
import dayjs from 'dayjs';

function OnCallSchedule() {
  const [schedule, setSchedule] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    onCallApi.getSchedule(8)
      .then(res => setSchedule(res.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return (
    <div style={{ textAlign: 'center', padding: '40px', color: '#888' }}>Loading...</div>
  );

  return (
    <div>
      <div style={{
        background: '#fff',
        borderRadius: '10px',
        padding: '20px 24px',
        marginBottom: '16px',
        boxShadow: '0 1px 4px rgba(0,0,0,0.08)',
      }}>
        <h2 style={{ fontSize: '18px', color: '#1a2c4e', marginBottom: '4px' }}>
          On-Call Schedule
        </h2>
        <p style={{ fontSize: '13px', color: '#888' }}>
          Next 8 weeks — weekly rotation
        </p>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {schedule.map((week, index) => {
          const isCurrentWeek = index === 0;
          const hasConflict = week.hasConflict;

          return (
            <div key={week.weekStart} style={{
              background: hasConflict ? '#fff8e1' : '#fff',
              border: hasConflict ? '2px solid #ffb300' : '1px solid #eee',
              borderRadius: '10px',
              padding: '18px 20px',
              boxShadow: '0 1px 4px rgba(0,0,0,0.06)',
              display: 'flex',
              alignItems: 'center',
              gap: '16px',
              flexWrap: 'wrap',
            }}>
              <div style={{ flex: 1, minWidth: '200px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span style={{ fontWeight: '700', fontSize: '15px' }}>
                    {week.onCallMember.name}
                  </span>
                  {isCurrentWeek && (
                    <span style={{
                      padding: '2px 8px',
                      borderRadius: '20px',
                      fontSize: '11px',
                      fontWeight: '600',
                      background: '#1a2c4e',
                      color: '#fff',
                    }}>
                      Current Week
                    </span>
                  )}
                </div>
                <div style={{ fontSize: '13px', color: '#666', marginTop: '4px' }}>
                  {dayjs(week.weekStart).format('MMM D')} – {dayjs(week.weekEnd).format('MMM D, YYYY')}
                </div>
              </div>

              {hasConflict ? (
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  background: '#fff3cd',
                  border: '1px solid #ffb300',
                  borderRadius: '6px',
                  padding: '8px 14px',
                }}>
                  <span style={{ fontSize: '16px' }}>⚠️</span>
                  <div>
                    <div style={{ fontSize: '13px', fontWeight: '600', color: '#b8860b' }}>
                      On-call conflict
                    </div>
                    <div style={{ fontSize: '12px', color: '#888' }}>
                      Approved leave during this week
                    </div>
                  </div>
                </div>
              ) : (
                <span style={{
                  padding: '4px 12px',
                  borderRadius: '20px',
                  fontSize: '12px',
                  fontWeight: '600',
                  background: '#e8f5e9',
                  color: '#2e7d32',
                  border: '1px solid #a5d6a7',
                }}>
                  ✓ Available
                </span>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}

export default OnCallSchedule;
