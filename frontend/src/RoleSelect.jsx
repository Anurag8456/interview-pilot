import { useState } from 'react';
import api from './api';

const ROLES = [
    { key: 'SDE_INTERN', title: 'SDE Intern', blurb: 'Problem-solving, code reasoning, edge cases' },
    { key: 'DATA_ANALYST', title: 'Data Analyst', blurb: 'Metrics, business framing, tool fluency' },
    { key: 'FRONTEND_DEVELOPER', title: 'Frontend Developer', blurb: 'UI/UX reasoning, performance, accessibility' },
];

function RoleSelect({ onStart }) {
    const [selectedRole, setSelectedRole] = useState(null);
    const [candidateLabel, setCandidateLabel] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleBegin = async () => {
        setError('');
        if (!selectedRole) { setError('Choose a role to begin.'); return; }
        if (!candidateLabel.trim()) { setError('Enter a name so we can save your progress.'); return; }

        setLoading(true);
        try {
            const res = await api.post('/api/interview/start', {
                role: selectedRole,
                candidateLabel: candidateLabel.trim(),
            });
            onStart({
                sessionId: res.data.sessionId,
                role: selectedRole,
                candidateLabel: candidateLabel.trim(),
                questionNumber: res.data.questionNumber,
                questionText: res.data.questionText,
            });
        } catch (err) {
            console.error(err);
            setError('Could not start the interview. Confirm the backend is running.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="screen role-screen">
            <p className="eyebrow">INTERVIEWPILOT</p>
            <h1>Practice the interview<br />before it counts.</h1>
            <p className="lede">Pick a role. Answer five questions. Get an honest, structured report — the kind a real interviewer would give you.</p>

            <div className="role-grid">
                {ROLES.map((role) => (
                    <button
                        key={role.key}
                        className={`role-card ${selectedRole === role.key ? 'role-card-active' : ''}`}
                        onClick={() => setSelectedRole(role.key)}
                    >
                        <span className="role-title">{role.title}</span>
                        <span className="role-blurb">{role.blurb}</span>
                    </button>
                ))}
            </div>

            <div className="name-row">
                <label htmlFor="candidateLabel">Your name</label>
                <input
                    id="candidateLabel"
                    type="text"
                    placeholder="e.g. Anurag"
                    value={candidateLabel}
                    onChange={(e) => setCandidateLabel(e.target.value)}
                />
            </div>

            {error && <p className="error-text">{error}</p>}

            <button className="primary-button" onClick={handleBegin} disabled={loading}>
                {loading ? 'Preparing your interviewer…' : 'Begin Interview'}
            </button>
        </div>
    );
}

export default RoleSelect;