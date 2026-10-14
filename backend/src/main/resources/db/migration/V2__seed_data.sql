-- ===================================================
-- EXAMCONNECT: SEED DATA MIGRATION V2
-- ===================================================

-- Initial Admin and Students (BCrypt encrypted password: Admin@123 and Student@123)
-- Admin: $2a$10$w850r2sQ1KzJ3w1E/E8CceHkYfPqD.e/cQ3N9B.V4Z1A0M9S8K2L6 -> will also be handled by DataInitializer
-- Student: student1@examconnect.com / Student@123
INSERT INTO users (id, full_name, email, password, phone, role, enabled) VALUES
(1, 'System Administrator', 'admin@examconnect.com', '$2a$10$iMhG7U015l5zH7Q5.3b.AeVgNq9q4xR7w7H3c0b0uY9a7W5o4Z2X2', '+91 9876543210', 'ROLE_ADMIN', TRUE),
(2, 'Rahul Sharma', 'rahul.sharma@example.com', '$2a$10$iMhG7U015l5zH7Q5.3b.AeVgNq9q4xR7w7H3c0b0uY9a7W5o4Z2X2', '+91 9123456780', 'ROLE_STUDENT', TRUE),
(3, 'Priya Patel', 'priya.patel@example.com', '$2a$10$iMhG7U015l5zH7Q5.3b.AeVgNq9q4xR7w7H3c0b0uY9a7W5o4Z2X2', '+91 9876501234', 'ROLE_STUDENT', TRUE),
(4, 'Sai Krishna', 'sai.krishna@example.com', '$2a$10$iMhG7U015l5zH7Q5.3b.AeVgNq9q4xR7w7H3c0b0uY9a7W5o4Z2X2', '+91 9988776655', 'ROLE_STUDENT', TRUE);

-- Exam Categories
INSERT INTO exams (id, code, name, description, category, active) VALUES
(1, 'UPSC', 'UPSC Civil Services Examination', 'Premier examination in India for recruiting IAS, IPS, IFS, and Central Group A & B services.', 'National', TRUE),
(2, 'SSC', 'SSC CGL & CHSL Examination', 'Staff Selection Commission exams for various Group B and C posts in Ministries and Departments.', 'National', TRUE),
(3, 'BANKING', 'Banking Examinations (IBPS & SBI)', 'Recruitment exams for Probationary Officers (PO) and Clerks across Public Sector Banks.', 'Banking', TRUE),
(4, 'RAILWAYS', 'Railway Recruitment Board (RRB NTPC)', 'Recruitment for Non-Technical Popular Categories, Station Masters, and Goods Guards.', 'Railways', TRUE),
(5, 'TS_EXAMS', 'Telangana State Public Service (TSPSC)', 'State civil service exams for Telangana administration, revenue, and public departments.', 'State - Telangana', TRUE),
(6, 'AP_EXAMS', 'Andhra Pradesh Public Service (APPSC)', 'State civil service examinations for Andhra Pradesh Group 1, Group 2, and executive cadres.', 'State - Andhra Pradesh', TRUE);

-- Subjects
INSERT INTO subjects (id, exam_id, name, code, description, active) VALUES
(1, 1, 'Indian Polity & Governance', 'GS-POLITY', 'Constitution, Political System, Panchayati Raj, Public Policy, Rights Issues.', TRUE),
(2, 1, 'Modern Indian History', 'GS-HIST', 'Indian National Movement, Freedom struggle, Socio-religious reform movements.', TRUE),
(3, 1, 'General Science & Tech', 'GS-SCI', 'Developments and applications of science, IT, Space, Biotechnology, Defense.', TRUE),
(4, 2, 'Quantitative Aptitude', 'SSC-QA', 'Number systems, Algebra, Geometry, Trigonometry, Mensuration, Data Interpretation.', TRUE),
(5, 2, 'General Intelligence & Reasoning', 'SSC-REAS', 'Analogy, Classification, Series, Coding-Decoding, Blood Relations, Syllogism.', TRUE),
(6, 2, 'English Comprehension', 'SSC-ENG', 'Grammar, Vocabulary, Reading Comprehension, Error Spotting, Cloze Test.', TRUE),
(7, 3, 'Banking & Financial Awareness', 'BANK-GA', 'RBI functions, Monetary Policy, Financial Inclusion, NPA, Basel Norms, Banking Terms.', TRUE),
(8, 3, 'Data Analysis & Interpretation', 'BANK-DI', 'Tabular, Pie, Bar charts, Radar charts, Caselets, Missing Data, Probability.', TRUE),
(9, 4, 'General Awareness & Railways GK', 'RRB-GK', 'Indian Railways history, Current affairs, General Science, Indian Geography.', TRUE),
(10, 5, 'Telangana Movement & State Formation', 'TS-MOV', 'Historical background of Telangana, 1969 agitation, bifurcation process, and state culture.', TRUE),
(11, 6, 'AP History & Economy', 'AP-HIST', 'Andhra movement, bifurcation act 2014, economic development, irrigation projects.', TRUE);

-- Topics
INSERT INTO topics (id, subject_id, name, description, active) VALUES
(1, 1, 'Preamble & Fundamental Rights', 'Articles 12 to 35, basic structure doctrine, judicial review.', TRUE),
(2, 1, 'Directive Principles & Fundamental Duties', 'Articles 36 to 51A, socialist, Gandhian and liberal-intellectual principles.', TRUE),
(3, 1, 'Parliament & Union Executive', 'President, Prime Minister, Lok Sabha, Rajya Sabha legislative procedures.', TRUE),
(4, 2, 'Revolt of 1857 & Early Resistance', 'Causes, leaders, failure reasons, and administrative changes post-1857.', TRUE),
(5, 2, 'Gandhian Era & Mass Movements', 'Non-Cooperation, Civil Disobedience, Quit India Movement.', TRUE),
(6, 4, 'Percentage & Profit & Loss', 'Formulas, shortcut techniques, marked price and successive discounts.', TRUE),
(7, 4, 'Time, Speed and Distance', 'Relative speed, trains, boats and streams, circular tracks.', TRUE),
(8, 5, 'Syllogism & Logical Deductions', 'Venn diagrams, standard rules, Possibility cases, Either-Or cases.', TRUE),
(9, 6, 'Error Detection & Sentence Improvement', 'Subject-verb agreement, tenses, prepositions, modifiers.', TRUE),
(10, 7, 'Reserve Bank of India & Monetary Policy', 'Repo, Reverse Repo, CRR, SLR, MSF, MPC structure.', TRUE),
(11, 10, 'Telangana Statehood Agitations (1969 & 2009)', 'Key events, student unions, Osmania University protests, Srikrishna Committee.', TRUE),
(12, 11, 'AP Reorganisation Act 2014', 'Bifurcation provisions, special category status, capital city developments.', TRUE);

-- Study Materials
INSERT INTO study_materials (id, title, description, material_type, file_url, file_name, file_size, content_text, exam_id, subject_id, topic_id, created_by, active) VALUES
(1, 'Comprehensive Indian Polity: Fundamental Rights & Duties', 'In-depth breakdown of Articles 12-51A with key Supreme Court landmark judgments (Kesavananda Bharati, Minerva Mills, Maneka Gandhi).', 'TEXT_NOTES', NULL, NULL, NULL, 'The Constitution of India guarantees Fundamental Rights under Part III (Articles 12-35). These rights are justifiable and protected by the Supreme Court under Article 32 (Heart and Soul of the Constitution according to Dr. B.R. Ambedkar). The Six fundamental rights are: 1. Right to Equality (Art 14-18), 2. Right to Freedom (Art 19-22), 3. Right against Exploitation (Art 23-24), 4. Right to Freedom of Religion (Art 25-28), 5. Cultural and Educational Rights (Art 29-30), 6. Right to Constitutional Remedies (Art 32). Part IV contains Directive Principles of State Policy (Articles 36-51) which are non-justiciable but fundamental in governance.', 1, 1, 1, 1, TRUE),
(2, 'Modern Indian History: Gandhian Movements Timeline (1915-1947)', 'Chronological summary of Mahatma Gandhi arrival in India, Champaran, Kheda, Ahmedabad Mill strike, Non-Cooperation, Salt Satyagraha, and Quit India.', 'TEXT_NOTES', NULL, NULL, NULL, 'Mahatma Gandhi returned to India from South Africa on 9th January 1915 (Pravasi Bharatiya Divas). Early experiments: 1917 Champaran Satyagraha (First Civil Disobedience against Tinkathia system), 1918 Ahmedabad Mill Strike (First Hunger Strike), 1918 Kheda Satyagraha (First Non-Cooperation). Major movements: Non-Cooperation Movement (1920-1922) suspended after Chauri Chaura incident. Civil Disobedience Movement (1930) started with Dandi March on March 12, 1930. Quit India Movement (1942) with the mantra "Do or Die" announced at Gowalia Tank Maidan.', 1, 2, 5, 1, TRUE),
(3, 'Quantitative Aptitude Master Formula Sheet', 'Quick revision cheat sheet for Percentages, Profit-Loss, Time-Speed-Distance, and Compound Interest for SSC CGL & CHSL.', 'SUMMARY', NULL, NULL, NULL, 'Key Quantitative Formulas:\n1. Profit % = (Profit / Cost Price) * 100\n2. Selling Price = CP * (100 + Profit%) / 100\n3. Discount % = (Discount / Marked Price) * 100\n4. Speed = Distance / Time; Convert km/h to m/s: multiply by 5/18; Convert m/s to km/h: multiply by 18/5\n5. Relative speed in opposite direction = S1 + S2; same direction = |S1 - S2|\n6. Simple Interest = (P * R * T) / 100\n7. Compound Interest = P(1 + R/100)^T - P\n8. Difference between CI and SI for 2 years = P * (R/100)^2.', 2, 4, 6, 1, TRUE),
(4, 'RBI Monetary Policy & Key Policy Rates Handbook', 'Complete guide to qualitative and quantitative tools used by RBI: Repo Rate, Reverse Repo, MSF, Bank Rate, CRR, and SLR.', 'TEXT_NOTES', NULL, NULL, NULL, 'Monetary Policy tools of the Reserve Bank of India (RBI):\nQuantitative Tools:\n1. Cash Reserve Ratio (CRR): Percentage of Net Demand and Time Liabilities (NDTL) banks must park with RBI as cash without earning interest.\n2. Statutory Liquidity Ratio (SLR): Percentage of NDTL banks must maintain in liquid assets like gold or government securities.\n3. Repo Rate: The rate at which commercial banks borrow short-term funds from RBI against collateral securities.\n4. Reverse Repo Rate: The rate at which RBI absorbs liquidity from banks.\n5. Marginal Standing Facility (MSF): Overnight emergency borrowing window for scheduled banks.\nQualitative Tools: Margin requirements, moral suasion, credit rationing, and direct action.', 3, 7, 10, 1, TRUE),
(5, 'Telangana Movement: Crucial Milestones & Accords', 'Essential notes on Gentlemen Agreement (1956), Six Point Formula (1973), Million March (2011), and Srikrishna Committee Report.', 'TEXT_NOTES', NULL, NULL, NULL, 'The Gentlemen Agreement was signed on February 20, 1956, in Delhi between leaders of Andhra and Telangana before the formation of Andhra Pradesh on November 1, 1956. Key protections guaranteed included Cabinet distribution (40% Telangana representation), Telugana Regional Committee, and preference in employment. The 1969 agitation was spearheaded by Osmania University students demanding implementation of safeguards. The final phase (2009-2014) led by K. Chandrashekar Rao and TJAC saw mass mobilizations: Million March (10 March 2011), Sadak Bandh, and Sakala Janula Samme culminating in the Andhra Pradesh Reorganisation Act 2014.', 5, 10, 11, 1, TRUE);

-- Practice Questions (30+ questions with verified answers, options, and explanations)
-- Question 1
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(1, 'Which Article of the Indian Constitution is referred to as the "Heart and Soul of the Constitution" by Dr. B.R. Ambedkar?', 1, 1, 1, 'C', 'Dr. B.R. Ambedkar called Article 32 (Right to Constitutional Remedies) the heart and soul of the Constitution because it empowers citizens to directly approach the Supreme Court for enforcement of fundamental rights.', 'EASY', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(1, 'A', 'Article 14'),
(1, 'B', 'Article 19'),
(1, 'C', 'Article 32'),
(1, 'D', 'Article 21');

-- Question 2
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(2, 'The basic structure doctrine of the Indian Constitution was propounded by the Supreme Court in which landmark case?', 1, 1, 1, 'B', 'The basic structure doctrine was established in the landmark Kesavananda Bharati vs State of Kerala case (1973), holding that Parliament cannot alter the basic structure of the Constitution via Article 368 amendments.', 'MEDIUM', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(2, 'A', 'Golaknath Case (1967)'),
(2, 'B', 'Kesavananda Bharati Case (1973)'),
(2, 'C', 'Minerva Mills Case (1980)'),
(2, 'D', 'Maneka Gandhi Case (1978)');

-- Question 3
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(3, 'Which Constitutional Amendment Act added the Fundamental Duties to Part IVA of the Indian Constitution?', 1, 1, 2, 'A', 'Fundamental Duties were added by the 42nd Constitutional Amendment Act of 1976 upon the recommendation of the Swaran Singh Committee. 10 duties were added originally; the 11th was added by the 86th Amendment (2002).', 'EASY', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(3, 'A', '42nd Constitutional Amendment Act, 1976'),
(3, 'B', '44th Constitutional Amendment Act, 1978'),
(3, 'C', '52nd Constitutional Amendment Act, 1985'),
(3, 'D', '86th Constitutional Amendment Act, 2002');

-- Question 4
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(4, 'Who presided over the historic Belgaum session of the Indian National Congress in 1924?', 1, 2, 5, 'D', 'Mahatma Gandhi presided over only one session of the Indian National Congress, which was held at Belgaum (Karnataka) in December 1924.', 'EASY', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(4, 'A', 'Jawaharlal Nehru'),
(4, 'B', 'Subhas Chandra Bose'),
(4, 'C', 'Sardar Vallabhbhai Patel'),
(4, 'D', 'Mahatma Gandhi');

-- Question 5
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(5, 'The Champaran Satyagraha of 1917 was launched primarily against which exploitative agricultural system?', 1, 2, 5, 'B', 'Champaran Satyagraha in Bihar was launched to protest against the Tinkathia system, where European planters forced peasants to cultivate indigo on 3/20th parts of their total land.', 'MEDIUM', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(5, 'A', 'Ryotwari system'),
(5, 'B', 'Tinkathia system'),
(5, 'C', 'Mahalwari system'),
(5, 'D', 'Zamindari system');

-- Question 6
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(6, 'If an article is sold at Rs. 480 at a profit of 20%, what was its Cost Price (CP)?', 2, 4, 6, 'A', 'Cost Price = (Selling Price * 100) / (100 + Profit%) = (480 * 100) / 120 = Rs. 400.', 'EASY', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(6, 'A', 'Rs. 400'),
(6, 'B', 'Rs. 384'),
(6, 'C', 'Rs. 420'),
(6, 'D', 'Rs. 450');

-- Question 7
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(7, 'A train 180 meters long is running at a speed of 54 km/h. How much time will it take to cross a telephone pole?', 2, 4, 7, 'C', 'Speed = 54 * (5/18) = 15 m/s. Time to cross pole = Distance / Speed = 180 / 15 = 12 seconds.', 'MEDIUM', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(7, 'A', '10 seconds'),
(7, 'B', '15 seconds'),
(7, 'C', '12 seconds'),
(7, 'D', '18 seconds');

-- Question 8
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(8, 'In a certain code language, "ROSE" is written as "6821" and "CHAIR" is written as "73456". How will "SEARCH" be written?', 2, 5, 8, 'B', 'From mapping: S=2, E=1, A=4, R=6, C=7, H=3. Therefore, SEARCH = 214673.', 'MEDIUM', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(8, 'A', '214674'),
(8, 'B', '214673'),
(8, 'C', '214573'),
(8, 'D', '241673');

-- Question 9
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(9, 'Choose the correctly spelled word:', 2, 6, 9, 'C', '"Accommodation" is spelled with double "c" and double "m".', 'EASY', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(9, 'A', 'Acommodation'),
(9, 'B', 'Accomodation'),
(9, 'C', 'Accommodation'),
(9, 'D', 'Acomodation');

-- Question 10
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(10, 'What is the minimum reserve that commercial banks are required to maintain with the Reserve Bank of India called?', 3, 7, 10, 'A', 'Cash Reserve Ratio (CRR) is the specified minimum percentage of a commercial bank’s total Net Demand and Time Liabilities (NDTL) that must be held in cash with the RBI.', 'EASY', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(10, 'A', 'Cash Reserve Ratio (CRR)'),
(10, 'B', 'Statutory Liquidity Ratio (SLR)'),
(10, 'C', 'Reverse Repo Rate'),
(10, 'D', 'Capital Adequacy Ratio (CAR)');

-- Question 11
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(11, 'Which committee recommended the formation of the Monetary Policy Committee (MPC) in India?', 3, 7, 10, 'C', 'The Urjit Patel Committee (2014) recommended setting up a 6-member Monetary Policy Committee to determine the policy repo rate targeting CPI inflation at 4% +/- 2%.', 'HARD', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(11, 'A', 'Narasimham Committee'),
(11, 'B', 'Raghuram Rajan Committee'),
(11, 'C', 'Urjit Patel Committee'),
(11, 'D', 'Bimal Jalan Committee');

-- Question 12
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(12, 'In which year was the Gentlemen Agreement signed between Andhra and Telangana leaders?', 5, 10, 11, 'B', 'The Gentlemen Agreement was signed on February 20, 1956 at Hyderabad House in New Delhi to ensure safeguards for Telangana within the unified Andhra Pradesh state.', 'EASY', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(12, 'A', '1948'),
(12, 'B', '1956'),
(12, 'C', '1969'),
(12, 'D', '1972');

-- Question 13
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(13, 'The famous "Million March" in the history of the Telangana Statehood agitation took place on which date?', 5, 10, 11, 'A', 'The historic Million March was held on March 10, 2011 on Tank Bund in Hyderabad, organized by Telangana Joint Action Committee (TJAC).', 'MEDIUM', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(13, 'A', '10th March 2011'),
(13, 'B', '9th December 2009'),
(13, 'C', '2nd June 2014'),
(13, 'D', '29th November 2009');

-- Question 14
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(14, 'Under the Andhra Pradesh Reorganisation Act 2014, how many districts were allocated to the newly formed Telangana state at inception?', 6, 11, 12, 'C', 'At inception on 2nd June 2014, Telangana consisted of 10 districts from the former united state of Andhra Pradesh (later reorganized into 33 districts).', 'EASY', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(14, 'A', '8'),
(14, 'B', '9'),
(14, 'C', '10'),
(14, 'D', '13');

-- Question 15
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(15, 'Which city was designated as the common capital of Andhra Pradesh and Telangana for a period not exceeding 10 years?', 6, 11, 12, 'D', 'According to Section 5 of the AP Reorganisation Act 2014, Hyderabad was declared the common capital for both states for a maximum period of 10 years.', 'EASY', 1.0, 0.25);
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(15, 'A', 'Vijayawada'),
(15, 'B', 'Visakhapatnam'),
(15, 'C', 'Warangal'),
(15, 'D', 'Hyderabad');

-- Questions 16 to 30 for full depth & comprehensive mock test testing
INSERT INTO practice_questions (id, question_text, exam_id, subject_id, topic_id, correct_option, explanation, difficulty, marks, negative_marks) VALUES
(16, 'Who was the first Governor-General of independent India?', 1, 2, 4, 'B', 'Lord Mountbatten served as the first Governor-General of independent India from August 1947 to June 1948. C. Rajagopalachari was the first Indian Governor-General.', 'MEDIUM', 1.0, 0.25),
(17, 'Which schedule of the Indian Constitution contains the list of recognized languages?', 1, 1, 1, 'C', 'The Eighth Schedule of the Constitution lists the 22 official languages of India.', 'EASY', 1.0, 0.25),
(18, 'The concept of "Directive Principles of State Policy" was borrowed from the constitution of which country?', 1, 1, 2, 'A', 'DPSPs in Part IV were borrowed from the Constitution of Ireland (which had adopted them from Spain).', 'EASY', 1.0, 0.25),
(19, 'What is the sum of angles in a quadrilateral?', 2, 4, 6, 'D', 'The sum of all interior angles in any four-sided polygon (quadrilateral) is 360 degrees.', 'EASY', 1.0, 0.25),
(20, 'Find the missing number in the series: 3, 7, 15, 31, 63, ?', 2, 5, 8, 'B', 'The pattern is: x * 2 + 1. (3*2+1=7, 7*2+1=15, 15*2+1=31, 31*2+1=63, 63*2+1=127).', 'EASY', 1.0, 0.25),
(21, 'Which is the oldest operational nuclear power plant in India?', 1, 3, 3, 'A', 'Tarapur Atomic Power Station in Maharashtra is the oldest commercial nuclear power station in India, commissioned in 1969.', 'MEDIUM', 1.0, 0.25),
(22, 'What does IFSC stand for in Indian Banking System?', 3, 7, 10, 'C', 'IFSC stands for Indian Financial System Code. It is an 11-character alphanumeric code used for NEFT, RTGS, and IMPS.', 'EASY', 1.0, 0.25),
(23, 'Which Indian state has the longest coastline?', 1, 2, 4, 'A', 'Gujarat has the longest coastline in mainland India, measuring approximately 1,600 km.', 'EASY', 1.0, 0.25),
(24, 'What is the chemical symbol for Gold?', 4, 9, 7, 'B', 'The chemical symbol for Gold is Au, derived from the Latin word "Aurum".', 'EASY', 1.0, 0.25),
(25, 'Where is the headquarters of the South Central Railway zone located?', 4, 9, 7, 'C', 'The South Central Railway (SCR) zone headquarters is located at Rail Nilayam in Secunderabad.', 'EASY', 1.0, 0.25),
(26, 'In which year did the Dandi March take place?', 1, 2, 5, 'D', 'Mahatma Gandhi started the Dandi March from Sabarmati Ashram to Dandi on 12th March 1930 and reached Dandi on 6th April 1930.', 'EASY', 1.0, 0.25),
(27, 'If a person walks 4 km North, then turns right and walks 3 km, what is the straight distance from the starting point?', 2, 5, 8, 'A', 'Using Pythagoras theorem: Distance = sqrt(4^2 + 3^2) = sqrt(16 + 9) = sqrt(25) = 5 km.', 'EASY', 1.0, 0.25),
(28, 'Which Article deals with the imposition of President Rule in a State?', 1, 1, 3, 'B', 'Article 356 empowers the President to impose President Rule in a state upon failure of constitutional machinery.', 'MEDIUM', 1.0, 0.25),
(29, 'What is the full form of NPA in banking terminology?', 3, 7, 10, 'B', 'NPA stands for Non-Performing Asset, referring to a loan or advance where interest or principal remains overdue for 90 days or more.', 'EASY', 1.0, 0.25),
(30, 'Which river is known as the "Dakshin Ganga" or Ganges of the South?', 1, 2, 4, 'C', 'Godavari is the second longest river in India and is called Dakshin Ganga due to its size and spiritual significance.', 'EASY', 1.0, 0.25);

-- Question options for questions 16 to 30
INSERT INTO question_options (question_id, option_key, option_text) VALUES
(16, 'A', 'C. Rajagopalachari'), (16, 'B', 'Lord Mountbatten'), (16, 'C', 'Dr. Rajendra Prasad'), (16, 'D', 'Jawaharlal Nehru'),
(17, 'A', 'Sixth Schedule'), (17, 'B', 'Seventh Schedule'), (17, 'C', 'Eighth Schedule'), (17, 'D', 'Ninth Schedule'),
(18, 'A', 'Ireland'), (18, 'B', 'USA'), (18, 'C', 'USSR'), (18, 'D', 'Australia'),
(19, 'A', '180 degrees'), (19, 'B', '270 degrees'), (19, 'C', '300 degrees'), (19, 'D', '360 degrees'),
(20, 'A', '125'), (20, 'B', '127'), (20, 'C', '129'), (20, 'D', '131'),
(21, 'A', 'Tarapur'), (21, 'B', 'Kudankulam'), (21, 'C', 'Rawatbhata'), (21, 'D', 'Kaiga'),
(22, 'A', 'Indian Fast Settlement Code'), (22, 'B', 'International Financial System Code'), (22, 'C', 'Indian Financial System Code'), (22, 'D', 'Interbank Funds Security Code'),
(23, 'A', 'Gujarat'), (23, 'B', 'Andhra Pradesh'), (23, 'C', 'Tamil Nadu'), (23, 'D', 'Maharashtra'),
(24, 'A', 'Ag'), (24, 'B', 'Au'), (24, 'C', 'Fe'), (24, 'D', 'Pb'),
(25, 'A', 'Vijayawada'), (25, 'B', 'Hyderabad'), (25, 'C', 'Secunderabad'), (25, 'D', 'Guntur'),
(26, 'A', '1920'), (26, 'B', '1928'), (26, 'C', '1932'), (26, 'D', '1930'),
(27, 'A', '5 km'), (27, 'B', '7 km'), (27, 'C', '6 km'), (27, 'D', '8 km'),
(28, 'A', 'Article 352'), (28, 'B', 'Article 356'), (28, 'C', 'Article 360'), (28, 'D', 'Article 368'),
(29, 'A', 'Net Payable Account'), (29, 'B', 'Non-Performing Asset'), (29, 'C', 'National Payment Authorization'), (29, 'D', 'Non-Profitable Asset'),
(30, 'A', 'Krishna'), (30, 'B', 'Kaveri'), (30, 'C', 'Godavari'), (30, 'D', 'Mahanadi');

-- Mock Tests
INSERT INTO mock_tests (id, title, description, exam_id, duration_minutes, total_questions, total_marks, passing_marks, negative_marking_per_question, active) VALUES
(1, 'UPSC Prelims Full Mock Test 01 - General Studies Paper I', 'Comprehensive GS-1 simulated test covering Polity, Modern History, Science & Technology, and Indian Geography.', 1, 20, 10, 20.0, 8.0, 0.66, TRUE),
(2, 'SSC CGL Tier-I General Mock Examination 2026', 'Speed and accuracy test covering Quantitative Aptitude, Logical Reasoning, and English Comprehension with negative marking.', 2, 15, 8, 16.0, 6.0, 0.50, TRUE),
(3, 'TSPSC Group-1 Prelims Practice Mock Test', 'Specialized test focusing on Telangana Movement, State History, Governance, and General Studies.', 5, 15, 6, 12.0, 5.0, 0.33, TRUE);

-- Mock Test Questions Mapping
-- Test 1 (10 questions: 1, 2, 3, 4, 5, 16, 17, 18, 21, 28)
INSERT INTO mock_test_questions (mock_test_id, question_id, order_index) VALUES
(1, 1, 1), (1, 2, 2), (1, 3, 3), (1, 4, 4), (1, 5, 5),
(1, 16, 6), (1, 17, 7), (1, 18, 8), (1, 21, 9), (1, 28, 10);

-- Test 2 (8 questions: 6, 7, 8, 9, 19, 20, 27, 24)
INSERT INTO mock_test_questions (mock_test_id, question_id, order_index) VALUES
(2, 6, 1), (2, 7, 2), (2, 8, 3), (2, 9, 4),
(2, 19, 5), (2, 20, 6), (2, 27, 7), (2, 24, 8);

-- Test 3 (6 questions: 12, 13, 14, 15, 25, 30)
INSERT INTO mock_test_questions (mock_test_id, question_id, order_index) VALUES
(3, 12, 1), (3, 13, 2), (3, 14, 3), (3, 15, 4), (3, 25, 5), (3, 30, 6);

-- Doubts and Discussions
INSERT INTO doubts (id, title, description, user_id, exam_id, subject_id, topic_id, has_accepted_answer, accepted_answer_id, answers_count, views_count) VALUES
(1, 'Difference between Article 32 and Article 226 writ jurisdiction', 'Can someone please clarify the primary differences between the writ powers of the Supreme Court under Article 32 and High Courts under Article 226? Which one has a wider scope?', 2, 1, 1, 1, TRUE, 1, 2, 45),
(2, 'Best shortcut method for relative speed questions with trains crossing bridges', 'I struggle with calculations when two trains of different lengths moving in opposite directions cross a platform or each other. Is there a consistent step-by-step formula?', 3, 2, 4, 7, FALSE, NULL, 1, 32),
(3, 'Gentlemen Agreement 1956 key points for TSPSC Mains', 'What are the most crucial 5 points of the Gentlemen Agreement that we must mention in descriptive Mains answers?', 4, 5, 10, 11, FALSE, NULL, 1, 28);

-- Answers
INSERT INTO answers (id, doubt_id, user_id, content, is_accepted, upvotes_count) VALUES
(1, 1, 3, 'Here are the key differences:\n1. Scope: Article 32 can be invoked ONLY for Fundamental Rights violation. Article 226 can be invoked for Fundamental Rights as well as ordinary legal rights. Hence, High Court writ jurisdiction is wider in scope.\n2. Territorial Jurisdiction: Supreme Court jurisdiction extends throughout India; High Court is restricted to its state boundary (or where cause of action arises).\n3. Duty vs Discretion: Under Article 32, the Supreme Court cannot refuse remedy as Article 32 is itself a Fundamental Right! Under Article 226, remedy is discretionary for High Courts.', TRUE, 12),
(2, 1, 4, 'Adding to Priya excellent answer: Dr. Ambedkar called Art 32 the cornerstone. Also remember that in Chandra Kumar case (1997), SC held that writ jurisdiction under both 32 and 226 is part of the basic structure of the Constitution.', FALSE, 5),
(3, 2, 2, 'Remember these two golden rules:\n1. Total Distance = Length of Train 1 + Length of Train 2 (or Length of Train + Length of Platform). Distance is always added!\n2. Relative Speed: If opposite direction -> Add speeds (S1 + S2). If same direction -> Subtract speeds (|S1 - S2|).\nConvert km/h to m/s by multiplying with 5/18 before dividing distance by speed.', FALSE, 8),
(4, 3, 2, 'Top 5 points for Gentlemen Agreement (20 Feb 1956):\n1. Expenditure on Telangana was to be secured in proportion to its population.\n2. Prohibition was to be implemented in Telangana according to local wishes.\n3. Cabinet distribution: Chief Minister from Andhra implies Deputy CM from Telangana (and vice versa); at least 2 out of 5 specified portfolios.\n4. Telangana Regional Committee (TRC) with statutory power on legislation.\n5. Domicile rules: 12-year residential qualification for public employment in Telangana.', FALSE, 9);

-- Update accepted_answer_id on doubt 1
UPDATE doubts SET accepted_answer_id = 1 WHERE id = 1;

-- Answer Votes
INSERT INTO answer_votes (answer_id, user_id) VALUES
(1, 2),
(1, 4),
(3, 3),
(4, 4);

-- Initial AI Knowledge Document & Chunks for RAG
INSERT INTO ai_documents (id, title, description, file_name, file_url, exam_id, subject_id, status, chunk_count) VALUES
(1, 'Indian Constitution & Fundamental Rights Compendium', 'Official reference guide on Part III of the Indian Constitution, writ jurisdictions, and fundamental rights doctrine.', 'constitution_part3_guide.pdf', '/uploads/materials/constitution_part3_guide.pdf', 1, 1, 'PROCESSED', 4),
(2, 'Modern Indian Freedom Struggle & Leaders Compendium', 'Comprehensive overview of Gandhian movements, revolutionary nationalists, and constitutional milestones.', 'indian_national_movement.pdf', '/uploads/materials/indian_national_movement.pdf', 1, 2, 'PROCESSED', 3);

INSERT INTO ai_document_chunks (id, document_id, chunk_index, page_number, content, metadata_json, embedding_json) VALUES
(1, 1, 1, 1, 'Part III of the Constitution of India covers Articles 12 through 35, enshrining the Fundamental Rights. These rights are protected against state violation. Article 12 defines "State" to include the Government and Parliament of India, state legislatures, and all local or other authorities within the territory of India or under the control of the Government of India. Article 13 mandates that all laws inconsistent with or in derogation of Fundamental Rights shall be null and void to the extent of such inconsistency.', '{"docTitle":"Indian Constitution & Fundamental Rights Compendium","page":1,"topic":"Fundamental Rights"}', NULL),
(2, 1, 2, 2, 'Article 14 guarantees equality before the law and equal protection of the laws within the territory of India. Article 19 guarantees six basic democratic freedoms: freedom of speech and expression, peaceful assembly without arms, forming associations or unions, moving freely throughout India, residing and settling anywhere in India, and practicing any profession, trade, or business, subject to reasonable restrictions.', '{"docTitle":"Indian Constitution & Fundamental Rights Compendium","page":2,"topic":"Right to Equality and Freedom"}', NULL),
(3, 1, 3, 3, 'Article 21 provides that no person shall be deprived of his life or personal liberty except according to procedure established by law. The landmark Maneka Gandhi case (1978) introduced the principle of "due process of law" requiring procedures to be just, fair, and reasonable. The Supreme Court has interpreted Article 21 to include right to clean environment, right to privacy (Puttaswamy judgment 2017), right to education, and right to dignified livelihood.', '{"docTitle":"Indian Constitution & Fundamental Rights Compendium","page":3,"topic":"Article 21 and Landmark Judgments"}', NULL),
(4, 1, 4, 4, 'Article 32 provides the Right to Constitutional Remedies. The Supreme Court has the power to issue prerogative writs in the nature of Habeas Corpus (to produce the body of an unlawfully detained person), Mandamus (to command a public official to perform their statutory duty), Prohibition (to prevent an inferior tribunal from exceeding jurisdiction), Certiorari (to quash orders of an inferior tribunal), and Quo-Warranto (to challenge unauthorized occupation of a public office).', '{"docTitle":"Indian Constitution & Fundamental Rights Compendium","page":4,"topic":"Writ Jurisdictions"}', NULL),
(5, 2, 1, 1, 'The Non-Cooperation Movement was launched on 1 August 1920 by Mahatma Gandhi following the Jallianwala Bagh Massacre (13 April 1919) and the Rowlatt Act. Key programs included boycott of foreign goods, surrender of British titles and honors, boycott of government schools and colleges, and promotion of Khadi. It was called off abruptly in February 1922 following the violent Chauri Chaura incident in Gorakhpur, Uttar Pradesh.', '{"docTitle":"Modern Indian Freedom Struggle & Leaders Compendium","page":1,"topic":"Non-Cooperation Movement"}', NULL),
(6, 2, 2, 2, 'The Civil Disobedience Movement began with the historic Salt Satyagraha or Dandi March on 12 March 1930. Mahatma Gandhi along with 78 chosen volunteers marched 240 miles from Sabarmati Ashram to the coastal village of Dandi in Gujarat. On 6 April 1930, Gandhi picked up a handful of natural salt, breaking the British salt monopoly law and sparking nation-wide civil disobedience including boycott of foreign cloth and non-payment of taxes.', '{"docTitle":"Modern Indian Freedom Struggle & Leaders Compendium","page":2,"topic":"Salt March and Civil Disobedience"}', NULL),
(7, 2, 3, 3, 'The Quit India Movement, also called the August Kranti, was launched on 8 August 1942 at the Bombay session of the All-India Congress Committee at Gowalia Tank Maidan. Mahatma Gandhi delivered his famous "Do or Die" speech. The British immediately arrested all top leaders in Operation Zero Hour, but the movement continued heroically as an underground rebellion led by Aruna Asaf Ali, Jayaprakash Narayan, Ram Manohar Lohia, and Usha Mehta (operating underground radio).', '{"docTitle":"Modern Indian Freedom Struggle & Leaders Compendium","page":3,"topic":"Quit India Movement"}', NULL);
