-- contest
INSERT INTO contest (id, name, start_time, end_time)
VALUES (1, 'Sample Contest', CURRENT_TIMESTAMP, DATEADD('DAY', 7, CURRENT_TIMESTAMP));

-- problems
INSERT INTO problem (id, contest_id, code, title, description, score)
VALUES
    (1, 1, 'SUMA', 'Simple Sum', 'Read two integers and print their sum', 100),
    (2, 1, 'FACT', 'Factorial', 'Read N and print N!', 100);

-- test cases
INSERT INTO test_case (problem_id, input_data, expected_output, sample_case)
VALUES
    (1, '2 3\n', '5', TRUE),
    (1, '100 250\n', '350', FALSE),
    (2, '5\n', '120', TRUE),
    (2, '1\n', '1', FALSE),
    (2, '0\n', '1', FALSE);
