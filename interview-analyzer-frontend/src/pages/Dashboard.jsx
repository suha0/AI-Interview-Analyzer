import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/axios";

function Dashboard() {
  const navigate = useNavigate();

  const user = JSON.parse(localStorage.getItem("user"));

  const [interviews, setInterviews] = useState([]);
  const [questions, setQuestions] = useState([]);

  const [form, setForm] = useState({
    title: "",
    targetRole: "",
    difficulty: "MEDIUM",
    count: 10,
  });

  useEffect(() => {
    fetchInterviews();
  }, []);

  const fetchInterviews = async () => {
    try {
      const response = await api.get("/interviews");
      setInterviews(response.data);
    } catch (error) {
      console.error("Failed to fetch interviews", error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    navigate("/");
  };

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const createInterview = async (e) => {
    e.preventDefault();

    try {
      const response = await api.post(
        "/interviews",
        form
      );

      alert(
        "Interview Created! ID: " +
          response.data.id
      );

      setForm({
        title: "",
        targetRole: "",
        difficulty: "MEDIUM",
        count: 10,
      });

      await fetchInterviews();
    } catch (error) {
      console.error(error);
      alert("Failed to create interview");
    }
  };

  const generateQuestions = async (
    interviewId
  ) => {
    try {
      const response = await api.post(
        `/interviews/${interviewId}/questions/generate`,
        {
          difficulty: form.difficulty,
          count: Number(form.count),
        }
      );

      setQuestions(response.data);

      alert(
        `${response.data.length} questions generated successfully`
      );
    } catch (error) {
      console.error(error);
      alert("Failed to generate questions");
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      {/* Header */}
      <div className="bg-white rounded-xl shadow p-6 mb-6">
        <h1 className="text-4xl font-bold mb-2">
          AI Interview Analyzer
        </h1>

        <p>Welcome {user?.email}</p>

        <button
          onClick={handleLogout}
          className="mt-4 bg-red-500 text-white px-4 py-2 rounded"
        >
          Logout
        </button>
      </div>

      {/* Create Interview */}
      <div className="bg-white rounded-xl shadow p-6 max-w-xl">
        <h2 className="text-2xl font-bold mb-4">
          Create Interview
        </h2>

        <form
          onSubmit={createInterview}
          className="space-y-4"
        >
          <input
            type="text"
            name="title"
            placeholder="Interview Title"
            value={form.title}
            className="w-full border p-3 rounded"
            onChange={handleChange}
          />

          <input
            type="text"
            name="targetRole"
            placeholder="Target Role"
            value={form.targetRole}
            className="w-full border p-3 rounded"
            onChange={handleChange}
          />

          <select
            name="difficulty"
            value={form.difficulty}
            className="w-full border p-3 rounded"
            onChange={handleChange}
          >
            <option value="EASY">
              EASY
            </option>
            <option value="MEDIUM">
              MEDIUM
            </option>
            <option value="HARD">
              HARD
            </option>
          </select>

          <input
            type="number"
            name="count"
            value={form.count}
            className="w-full border p-3 rounded"
            onChange={handleChange}
          />

          <button className="w-full bg-blue-600 text-white p-3 rounded">
            Create Interview
          </button>
        </form>
      </div>

      {/* Interview List */}
      <div className="bg-white rounded-xl shadow p-6 mt-6">
        <h2 className="text-2xl font-bold mb-4">
          My Interviews
        </h2>

        {interviews.length === 0 ? (
          <p>No interviews found.</p>
        ) : (
          interviews.map((interview) => (
            <div
              key={interview.id}
              className="border rounded-lg p-4 mb-4"
            >
              <h3 className="text-xl font-semibold">
                {interview.title}
              </h3>

              <p>
                Role: {interview.targetRole}
              </p>

              <p>
                Status: {interview.status}
              </p>

              <p>
                Interview ID: {interview.id}
              </p>

              <div className="flex gap-3 mt-4">
                <button
                  onClick={() =>
                    generateQuestions(
                      interview.id
                    )
                  }
                  className="bg-green-600 text-white px-4 py-2 rounded"
                >
                  Generate Questions
                </button>

                <button
                  onClick={() =>
                    navigate(
                      `/interview/${interview.id}`
                    )
                  }
                  className="bg-blue-600 text-white px-4 py-2 rounded"
                >
                  Start Interview
                </button>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Generated Questions */}
      {questions.length > 0 && (
        <div className="bg-white rounded-xl shadow p-6 mt-6">
          <h2 className="text-2xl font-bold mb-4">
            Generated Questions
          </h2>

          <div className="space-y-3">
            {questions.map((q) => (
              <div
                key={q.id}
                className="border rounded p-4"
              >
                <p className="font-semibold">
                  Q{q.position}. {q.prompt}
                </p>

                <p className="text-sm text-gray-500 mt-1">
                  Category: {q.category}
                </p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}

export default Dashboard;