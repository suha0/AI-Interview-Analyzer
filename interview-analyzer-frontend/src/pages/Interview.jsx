import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { FaMicrophone, FaStop } from "react-icons/fa";
import api from "../api/axios";

function Interview() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [questions, setQuestions] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);

  const [answerText, setAnswerText] = useState("");
  const [result, setResult] = useState(null);

  const [loading, setLoading] = useState(true);

  // Speech Recognition
  const [isListening, setIsListening] = useState(false);
  const [recognition, setRecognition] = useState(null);

  useEffect(() => {
    fetchQuestions();

    const SpeechRecognition =
      window.SpeechRecognition ||
      window.webkitSpeechRecognition;

    if (SpeechRecognition) {
      const recognitionInstance =
        new SpeechRecognition();

      recognitionInstance.continuous = true;
      recognitionInstance.interimResults = true;
      recognitionInstance.lang = "en-US";

      recognitionInstance.onresult = (event) => {
        let transcript = "";

        for (
          let i = event.resultIndex;
          i < event.results.length;
          i++
        ) {
          transcript += event.results[i][0].transcript;
        }

        setAnswerText(transcript);
      };

      recognitionInstance.onend = () => {
        setIsListening(false);
      };

      setRecognition(recognitionInstance);
    }
  }, []);

  const fetchQuestions = async () => {
    try {
      const response = await api.get(
        `/interviews/${id}/questions`
      );

      setQuestions(response.data);
    } catch (error) {
      console.error(error);
      alert("Failed to load questions");
    } finally {
      setLoading(false);
    }
  };

  const downloadReport = async () => {
    try {
      const response = await api.get(
        `/interviews/${id}/report`,
        {
          responseType: "blob",
        }
      );

      const blob = new Blob(
        [response.data],
        { type: "application/pdf" }
      );

      const url = window.URL.createObjectURL(blob);

      const link = document.createElement("a");

      link.href = url;
      link.download = `interview-report-${id}.pdf`;

      document.body.appendChild(link);
      link.click();

      link.remove();

      window.URL.revokeObjectURL(url);

    } catch (error) {
      console.error(error);
      alert("Failed to download report");
    }
  };

  const startListening = () => {
    if (!recognition) {
      alert(
        "Speech Recognition is not supported in this browser."
      );
      return;
    }

    setIsListening(true);
    recognition.start();
  };

  const stopListening = () => {
    if (recognition) {
      recognition.stop();
      setIsListening(false);
    }
  };

  const submitAnswer = async () => {
    const currentQuestion =
      questions[currentIndex];

    if (!answerText.trim()) {
      alert("Please enter an answer");
      return;
    }

    try {
      const response = await api.post(
        "/answers",
        {
          interviewId: Number(id),
          questionId: currentQuestion.id,
          answerText: answerText,
        }
      );

      setResult(response.data);
    } catch (error) {
      console.error(error);
      alert("Failed to submit answer");
    }
  };

  const nextQuestion = () => {
    stopListening();

    setAnswerText("");
    setResult(null);

    if (
      currentIndex <
      questions.length - 1
    ) {
      setCurrentIndex(
        currentIndex + 1
      );
    } else {
      alert("Interview Completed!");
      navigate("/dashboard");
    }
  };

  if (loading) {
    return (
      <div className="p-8">
        Loading Questions...
      </div>
    );
  }

  if (questions.length === 0) {
    return (
      <div className="p-8">
        No Questions Found
      </div>
    );
  }

  const currentQuestion =
    questions[currentIndex];

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <div className="bg-white rounded-xl shadow p-6">
        <h1 className="text-3xl font-bold mb-4">
          Interview Session
        </h1>

        <p className="mb-2">
          Question {currentIndex + 1} of{" "}
          {questions.length}
        </p>

        <div className="bg-gray-50 p-4 rounded mb-4">
          <p className="text-sm text-gray-500">
            Category:{" "}
            {currentQuestion.category}
          </p>

          <h2 className="text-xl font-semibold mt-2">
            {currentQuestion.prompt}
          </h2>
        </div>

        {/* Speech Controls */}
        <div className="flex gap-3 mb-4">
          {!isListening ? (
            <button
              onClick={startListening}
              className="flex items-center gap-2 bg-red-600 text-white px-4 py-2 rounded"
            >
              <FaMicrophone />
              Start Speaking
            </button>
          ) : (
            <button
              onClick={stopListening}
              className="flex items-center gap-2 bg-gray-700 text-white px-4 py-2 rounded"
            >
              <FaStop />
              Stop Recording
            </button>
          )}

          {isListening && (
            <span className="text-red-600 font-semibold flex items-center">
              🎤 Listening...
            </span>
          )}
        </div>

        <textarea
          rows="8"
          value={answerText}
          onChange={(e) =>
            setAnswerText(e.target.value)
          }
          className="w-full border rounded p-3"
          placeholder="Type or speak your answer here..."
        />

        {!result && (
          <button
            onClick={submitAnswer}
            className="mt-4 bg-blue-600 text-white px-6 py-3 rounded"
          >
            Submit Answer
          </button>
        )}

        {result && (
          <div className="mt-6">
            <div className="bg-green-50 border rounded p-4 mb-4">
              <h3 className="font-bold text-lg">
                Score
              </h3>

              <p>
                {result.score}/100
              </p>
            </div>

            <div className="bg-blue-50 border rounded p-4 mb-4">
              <h3 className="font-bold text-lg">
                Feedback
              </h3>

              <p>
                {result.feedback}
              </p>
            </div>
            
            {result.followUpQuestion && (
              <div className="bg-yellow-50 border rounded p-4 mb-4">
                <h3 className="font-bold text-lg">
                  AI Follow-Up Question
                </h3>

                <p>{result.followUpQuestion}</p>
              </div>
            )}

            <div className="bg-purple-50 border rounded p-4 mb-4">
              <h3 className="font-bold text-lg">
                Emotion Analysis
              </h3>

              <p>
                Emotion:{" "}
                {result.emotion}
              </p>

              <p>
                Confidence:{" "}
                {result.emotionScore}
              </p>

              <p className="mt-2">
                {result.emotionExplanation}
              </p>
            </div>

            <div className="flex gap-3">

              {currentIndex === questions.length - 1 ? (
                <>
                  <button
                    onClick={downloadReport}
                    className="bg-blue-600 text-white px-6 py-3 rounded"
                  >
                    Download PDF Report
                  </button>

                  <button
                    onClick={() => navigate("/dashboard")}
                    className="bg-green-600 text-white px-6 py-3 rounded"
                  >
                    Finish Interview
                  </button>
                </>
              ) : (
                  <button
                    onClick={nextQuestion}
                    className="bg-green-600 text-white px-6 py-3 rounded"
                  >
                    Next Question
                  </button>
                  )}

            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default Interview;