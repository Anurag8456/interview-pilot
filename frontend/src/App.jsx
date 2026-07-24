import { useState } from 'react';
import RoleSelect from './RoleSelect';
import InterviewChat from './InterviewChat';
import ReportCard from './ReportCard';
import './App.css';

function App() {
  const [screen, setScreen] = useState('select');
  const [sessionData, setSessionData] = useState(null);
  const [report, setReport] = useState(null);

  const handleStart = (data) => {
    setSessionData(data);
    setScreen('chat');
  };

  const handleNextQuestion = (nextQuestionNumber, nextQuestionText) => {
    setSessionData((prev) => ({ ...prev, questionNumber: nextQuestionNumber, questionText: nextQuestionText }));
  };

  const handleComplete = (finalReport) => {
    setReport(finalReport);
    setScreen('report');
  };

  const handleRestart = () => {
    setSessionData(null);
    setReport(null);
    setScreen('select');
  };

  return (
      <div className="app-shell">
        {screen === 'select' && <RoleSelect onStart={handleStart} />}
        {screen === 'chat' && sessionData && (
            <InterviewChat
                sessionId={sessionData.sessionId}
                questionNumber={sessionData.questionNumber}
                questionText={sessionData.questionText}
                onNextQuestion={handleNextQuestion}
                onComplete={handleComplete}
            />
        )}
        {screen === 'report' && report && (
            <ReportCard report={report} candidateLabel={sessionData.candidateLabel} onRestart={handleRestart} />
        )}
      </div>
  );
}

export default App;