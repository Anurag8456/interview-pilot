import { useState, useEffect, useRef } from 'react';
import api from './api';

const QUESTION_TIME_SECONDS = 90;

function TypedQuestion({ text }) {
    const [displayed, setDisplayed] = useState('');

    useEffect(() => {
        setDisplayed('');
        let i = 0;
        const interval = setInterval(() => {
            i++;
            setDisplayed(text.slice(0, i));
            if (i >= text.length) clearInterval(interval);
        }, 18);
        return () => clearInterval(interval);
    }, [text]);

    return <p className="question-text">{displayed}<span className="type-cursor">|</span></p>;
}

function CountdownRing({ questionNumber }) {
    const [secondsLeft, setSecondsLeft] = useState(QUESTION_TIME_SECONDS);

    useEffect(() => {
        setSecondsLeft(QUESTION_TIME_SECONDS);
        const interval = setInterval(() => {
            setSecondsLeft((s) => (s > 0 ? s - 1 : 0));
        }, 1000);
        return () => clearInterval(interval);
    }, [questionNumber]);

    const radius = 26;
    const circumference = 2 * Math.PI * radius;
    const pct = secondsLeft / QUESTION_TIME_SECONDS;
    const offset = circumference * (1 - pct);
    const urgency = secondsLeft <= 15 ? 'ring-urgent' : secondsLeft <= 40 ? 'ring-warn' : 'ring-calm';

    return (
        <svg width="64" height="64" viewBox="0 0 64 64" className={`countdown-ring ${urgency}`}>
            <circle cx="32" cy="32" r={radius} className="ring-track" />
            <circle
                cx="32" cy="32" r={radius}
                className="ring-progress"
                style={{ strokeDasharray: circumference, strokeDashoffset: offset }}
            />
            <text x="32" y="37" textAnchor="middle" className="ring-time">{secondsLeft}</text>
        </svg>
    );
}

function InterviewChat({ sessionId, questionNumber, questionText, onComplete, onNextQuestion }) {
    const [answerText, setAnswerText] = useState('');
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState('');
    const textareaRef = useRef(null);

    useEffect(() => {
        setAnswerText('');
        textareaRef.current?.focus();
    }, [questionNumber]);

    const handleSubmit = async () => {
        setError('');
        if (!answerText.trim()) { setError('Type an answer before submitting.'); return; }

        setSubmitting(true);
        try {
            const res = await api.post('/api/interview/answer', {
                sessionId, questionNumber, answerText: answerText.trim(),
            });
            if (res.data.isComplete) {
                onComplete(res.data.report);
            } else {
                onNextQuestion(res.data.nextQuestionNumber, res.data.nextQuestionText);
            }
        } catch (err) {
            console.error(err);
            setError('Something went wrong submitting your answer. Try again.');
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="screen chat-screen">
            <div className="progress-dots">
                {[1, 2, 3, 4, 5].map((n) => (
                    <span
                        key={n}
                        className={`dot ${n < questionNumber ? 'dot-done' : ''} ${n === questionNumber ? 'dot-current' : ''} ${n === 5 ? 'dot-curveball' : ''}`}
                    >
            {n === 5 ? '★' : n}
          </span>
                ))}
            </div>

            <div className="chat-card">
                <div className="chat-header">
                    <span className="question-label">Question {questionNumber} of 5</span>
                    <CountdownRing questionNumber={questionNumber} />
                </div>

                <TypedQuestion text={questionText} />

                <textarea
                    ref={textareaRef}
                    rows={8}
                    placeholder="Take your time — answer as you would in a real interview..."
                    value={answerText}
                    onChange={(e) => setAnswerText(e.target.value)}
                />

                {error && <p className="error-text">{error}</p>}

                <button className="primary-button" onClick={handleSubmit} disabled={submitting}>
                    {submitting ? 'Evaluating…' : questionNumber === 5 ? 'Submit Final Answer' : 'Submit Answer'}
                </button>
            </div>
        </div>
    );
}

export default InterviewChat;