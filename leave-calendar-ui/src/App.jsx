import { useState } from 'react';
import LeaveForm from './components/LeaveForm';
import LeaveList from './components/LeaveList';
import OnCallSchedule from './components/OnCallSchedule';
import './index.css';

function App() {
  const [activeTab, setActiveTab] = useState('leaves');
  const [refreshKey, setRefreshKey] = useState(0);

  const handleLeaveCreated = () => {
    setRefreshKey(prev => prev + 1);
  };

  return (
    <div style={{ maxWidth: '1100px', margin: '0 auto', padding: '24px' }}>
      <header style={{
        background: '#1a2c4e',
        color: 'white',
        padding: '20px 24px',
        borderRadius: '10px',
        marginBottom: '24px'
      }}>
        <h1 style={{ fontSize: '22px', fontWeight: 'bold' }}>
          Team Leave Calendar
        </h1>
        <p style={{ color: '#8ab4d4', marginTop: '4px', fontSize: '14px' }}>
          Manage leave requests and on-call rotation
        </p>
      </header>

      <nav style={{ display: 'flex', gap: '8px', marginBottom: '24px' }}>
        {['leaves', 'request', 'oncall'].map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            style={{
              padding: '10px 20px',
              borderRadius: '6px',
              border: 'none',
              background: activeTab === tab ? '#1a2c4e' : '#fff',
              color: activeTab === tab ? '#fff' : '#333',
              fontWeight: activeTab === tab ? 'bold' : 'normal',
              boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
              fontSize: '14px',
            }}
          >
            {tab === 'leaves' && 'Leave Requests'}
            {tab === 'request' && 'New Request'}
            {tab === 'oncall' && 'On-Call Schedule'}
          </button>
        ))}
      </nav>

      <main>
        {activeTab === 'leaves' && (
          <LeaveList key={refreshKey} />
        )}
        {activeTab === 'request' && (
          <LeaveForm onCreated={() => {
            handleLeaveCreated();
            setActiveTab('leaves');
          }} />
        )}
        {activeTab === 'oncall' && (
          <OnCallSchedule />
        )}
      </main>
    </div>
  );
}

export default App;
