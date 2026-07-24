import { useState, useEffect } from 'react';
import api from './api';

function CountUpScore({ value }) {
    const [display, setDisplay] = useState(0);

    useEffect(() => {
        let frame = 0;
        const totalFrames = 40;
        const interval = setInterval(() => {
            frame++;
            setDisplay(Math.min(value, (value * frame) / totalFrames));
            if (frame >= totalFrames) clearInterval(interval);
        }, 25);
        return () => clearInterval(interval);
    }, [value]);

    return <span className="score-hero-value">{display.toFixed(1)}</span>;
}

function ReportCard({ report, candidateLabel, onRestart }) {
    const [history, setHistory] = useState([]);

    useEffect(() => {
        api.get(`/api/interview/history/${encodeURIComponent(candidateLabel)}`)
            .then((res) => setHistory(res.data))
            .catch((err) => console.error(err));
    }, [candidateLabel]);

    const sortedScores = [...(report.questionScores || [])].sort((a, b) => a.questionNumber - b.questionNumber);

    return (
        <div className="screen report-screen">
            <div className="score-hero">
                <p className="eyebrow">YOUR REPORT</p>
                <CountUpScore value={report.overallScore} />
                <span className="score-hero-total">/ 10 overall</span>
            </div>

            {history.length > 1 && (
                <div className="trend-row">
                    <span className="field-label">Your progress</span>
                    <div className="trend-chips">
                        {history.slice().reverse().map((s, i) => (
                            <span key={s.id} className="trend-chip">
                {s.role.replace('_', ' ')} · Session {i + 1}
              </span>
                        ))}
                    </div>
                </div>
            )}

            <div className="question-breakdown">
                {sortedScores.map((q) => (
                    <div key={q.questionNumber} className={`breakdown-card ${q.questionNumber === report.weakestQuestionNumber ? 'breakdown-weakest' : ''}`}>
                        <div className="breakdown-top">
                            <span className="breakdown-q">Question {q.questionNumber}{q.questionNumber === 5 && ' · Curveball'}</span>
                            <span className="breakdown-score">{q.score.toFixed(1)}/10</span>
                        </div>
                        <div className="score-track">
                            <div className="score-fill" style={{ width: `${(q.score / 10) * 100}%` }} />
                        </div>
                        <p className="impression">"{q.interviewerImpression}"</p>
                        <div className="badge-row">
                            {q.strengths.map((s, i) => <span key={i} className="badge badge-strength">+ {s}</span>)}
                            {q.weaknesses.map((w, i) => <span key={i} className="badge badge-weakness">− {w}</span>)}
                        </div>
                        {q.questionNumber === report.weakestQuestionNumber && (
                            <div className="model-answer-box">
                                <span className="field-label">Model answer for this question</span>
                                <p>{report.modelAnswerForWeakest}</p>
                            </div>
                        )}
                    </div>
                ))}
            </div>

            <button className="primary-button secondary-variant" onClick={onRestart}>
                Practice Again
            </button>
        </div>
    );
}

export default ReportCard;