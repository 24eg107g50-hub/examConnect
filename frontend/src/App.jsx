
import { useEffect, useState } from "react";
import { getQuestions } from "./api.js";


function App() {
  const [questions, setQuestions] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [answers, setAnswers] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [retryCount, setRetryCount] = useState(0);

  useEffect(() => {
    let cancelled = false;

    async function loadQuestions() {
      setLoading(true);
      setError("");

      try {
        const result = await getQuestions(page, 10);

        if (!cancelled) {
          setQuestions(result.questions);
          setTotalPages(Math.max(1, result.totalPages));
          setTotalElements(result.totalElements);
        }
      } catch (err) {
        if (!cancelled) {
          setError(err.message || "Unable to connect to the server.");
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    loadQuestions();

    return () => {
      cancelled = true;
    };
  }, [page, retryCount]);

  function selectAnswer(questionId, optionKey) {
    setAnswers((previous) => ({
      ...previous,
      [questionId]: optionKey
    }));
  }

  function changePage(nextPage) {
    if (nextPage >= 0 && nextPage < totalPages) {
      setPage(nextPage);
      window.scrollTo({ top: 0, behavior: "smooth" });
    }
  }

  return (
    <div className="app">
      <header className="navbar">
        <a className="brand" href="#home">
          <span className="brand-icon">E</span>
          <span>ExamConnect</span>
        </a>

        <nav>
          <a href="#home">Home</a>
          <a href="#features">Features</a>
          <a href="#practice">Practice</a>
        </nav>

        <a className="nav-button" href="#practice">
          Start Learning
        </a>
      </header>

      <main>
        <section className="hero" id="home">
          <div className="hero-content">
            <span className="eyebrow">
              YOUR GOVERNMENT EXAM LEARNING PARTNER
            </span>

            <h1>
              Prepare smarter.
              <br />
              <span>Achieve more.</span>
            </h1>

            <p>
              Practice questions, explore study resources, and
              prepare confidently for competitive examinations.
            </p>

            <div className="hero-actions">
              <a className="primary-button" href="#practice">
                Explore Questions →
              </a>
              <a className="secondary-button" href="#features">
                Discover Features
              </a>
            </div>

            <div className="stats">
              <div>
                <strong>{totalElements}</strong>
                <span>Questions available</span>
              </div>
              <div>
                <strong>{Object.keys(answers).length}</strong>
                <span>Answers selected</span>
              </div>
              <div>
                <strong>24/7</strong>
                <span>Learn anytime</span>
              </div>
            </div>
          </div>

          <div className="hero-card">
            <div className="card-label">YOUR LEARNING JOURNEY</div>
            <div className="circle">
              <div>
                <strong>Learn</strong>
                <span>Practice daily</span>
              </div>
            </div>
            <h2>Small steps. Big achievements.</h2>
            <p>
              Build your knowledge one practice question at a time.
            </p>
            <a href="#practice" className="card-link">
              Start practicing →
            </a>
          </div>
        </section>

        <section className="features section" id="features">
          <div className="section-heading">
            <span className="eyebrow">MADE FOR YOUR SUCCESS</span>
            <h2>Everything you need to prepare</h2>
            <p>One place to organize your learning journey.</p>
          </div>

          <div className="feature-grid">
            <article className="feature-card">
              <div className="feature-icon purple">✎</div>
              <h3>Practice Questions</h3>
              <p>
                Answer questions loaded from your ExamConnect backend.
              </p>
            </article>

            <article className="feature-card">
              <div className="feature-icon green">▤</div>
              <h3>Study Materials</h3>
              <p>
                Prepare with organized study resources.
              </p>
            </article>

            <article className="feature-card">
              <div className="feature-icon orange">◷</div>
              <h3>Mock Tests</h3>
              <p>
                Build exam readiness with structured practice.
              </p>
            </article>
          </div>
        </section>

        <section className="practice section" id="practice">
          <div className="section-heading">
            <span className="eyebrow">TEST YOUR KNOWLEDGE</span>
            <h2>Practice questions</h2>
            <p>
              Questions are retrieved from your Spring Boot API.
            </p>
          </div>

          <div className="summary">
            <div>
              <strong>{totalElements}</strong>
              <span>Total questions</span>
            </div>
            <div>
              <strong>{Object.keys(answers).length}</strong>
              <span>Selected answers</span>
            </div>
            <div>
              <strong>{page + 1}/{totalPages}</strong>
              <span>Current page</span>
            </div>
          </div>

          {loading && (
            <div className="message">
              <div className="spinner" />
              <p>Loading questions...</p>
            </div>
          )}

          {!loading && error && (
            <div className="message error">
              <h3>Unable to load questions</h3>
              <p>{error}</p>
              <button
                className="primary-button"
                onClick={() => setRetryCount((count) => count + 1)}
              >
                Try again
              </button>
              <p className="hint">
                Check that Spring Boot is running on port 8080.
              </p>
            </div>
          )}

          {!loading && !error && questions.length === 0 && (
            <div className="message">
              No questions were returned by the API.
            </div>
          )}

          {!loading && !error && questions.length > 0 && (
            <div className="question-list">
              {questions.map((question, index) => {
                const id = question.id ?? `${page}-${index}`;
                const options = question.options ?? [];

                return (
                  <article className="question-card" key={id}>
                    <div className="question-top">
                      <span className="question-number">
                        Question {page * 10 + index + 1}
                      </span>
                      <span className="difficulty">
                        {question.difficulty ?? "PRACTICE"}
                      </span>
                    </div>

                    <div className="meta">
                      {question.examName && (
                        <span>{question.examName}</span>
                      )}
                      {question.subjectName && (
                        <span>{question.subjectName}</span>
                      )}
                    </div>

                    <h3>
                      {question.questionText ?? "Question text unavailable"}
                    </h3>

                    <div className="options">
                      {options.map((option, optionIndex) => {
                        const key =
                          option.optionKey ??
                          option.key ??
                          String(optionIndex);
                        const text =
                          option.optionText ??
                          option.text ??
                          "";
                        const selected = answers[id] === key;

                        return (
                          <button
                            type="button"
                            key={key}
                            className={`option ${selected ? "selected" : ""}`}
                            onClick={() => selectAnswer(id, key)}
                            aria-pressed={selected}
                          >
                            <span className="option-key">{key}</span>
                            <span>{text}</span>
                            <span className="check">
                              {selected ? "✓" : ""}
                            </span>
                          </button>
                        );
                      })}
                    </div>

                    <div className="question-footer">
                      <span>Marks: {question.marks ?? 1}</span>
                      <span>
                        Negative marks: {question.negativeMarks ?? 0}
                      </span>
                    </div>
                  </article>
                );
              })}
            </div>
          )}

          {!loading && !error && totalPages > 1 && (
            <div className="pagination">
              <button
                className="page-button"
                disabled={page === 0}
                onClick={() => changePage(page - 1)}
              >
                ← Previous
              </button>

              <span>Page {page + 1} of {totalPages}</span>

              <button
                className="page-button"
                disabled={page + 1 >= totalPages}
                onClick={() => changePage(page + 1)}
              >
                Next →
              </button>
            </div>
          )}

          <p className="note">
            Answer selections are currently stored in frontend state.
            Submitting answers for evaluation requires the backend
            practice submission endpoint and authentication.
          </p>
        </section>

        <section className="cta">
          <h2>Your exam preparation starts today.</h2>
          <p>Learn consistently. Practice confidently. Achieve more.</p>
          <a className="primary-button" href="#practice">
            Start Practicing →
          </a>
        </section>
      </main>

      <footer className="footer">
        <a className="brand" href="#home">
          <span className="brand-icon">E</span>
          ExamConnect
        </a>
        <p>Supporting your government exam preparation.</p>
        <span>© {new Date().getFullYear()} ExamConnect</span>
      </footer>
    </div>
  );
}

export default App;
