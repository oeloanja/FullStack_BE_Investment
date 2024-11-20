CREATE TABLE IF NOT EXISTS investment (
    investment_id SERIAL PRIMARY KEY,
    group_id INT NOT NULL,
    user_investor_id INT NOT NULL,
    account_investor_id INT NOT NULL,
    investment_amount DECIMAL(15, 2) NOT NULL,
    investment_date TIMESTAMP,
    expected_return_rate DECIMAL(5, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS invest_status (
    investment_id INT PRIMARY KEY,
    invest_status_type VARCHAR(20) NOT NULL,
    FOREIGN KEY (investment_id) REFERENCES investment(investment_id)
);

CREATE TABLE IF NOT EXISTS investment_actual_return_rate (
    investment_return_rate_id SERIAL PRIMARY KEY,
    investment_id INT NOT NULL,
    actual_return_rate DECIMAL(5, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (investment_id) REFERENCES investment(investment_id)
);

CREATE TABLE IF NOT EXISTS investment_portfolio (
    portfolio_id SERIAL PRIMARY KEY,
    user_investor_id INT NOT NULL,
    portfolio_name VARCHAR(100) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    target_return_rate DECIMAL(5, 2) NOT NULL,
    total_invested_amount DECIMAL(15, 2) NOT NULL,
    actual_return_value DECIMAL(15, 2) NOT NULL,
    actual_return_rate DECIMAL(5, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS settlement (
    settlement_id SERIAL PRIMARY KEY,
    investment_id INT NOT NULL,
    settlement_ratio DECIMAL(10, 8) NOT NULL,
    FOREIGN KEY (investment_id) REFERENCES investment(investment_id)
);

CREATE TABLE IF NOT EXISTS settlement_detail (
    settlement_id INT NOT NULL,
    settlement_times INT NOT NULL,
    settlement_date DATE NOT NULL,
    settlement_principal DECIMAL(15, 2) NOT NULL,
    settlement_profit DECIMAL(15, 2) NOT NULL,
    is_completed BOOLEAN NOT NULL DEFAULT false,
    PRIMARY KEY (settlement_id, settlement_times),
    FOREIGN KEY (settlement_id) REFERENCES settlement(settlement_id)
);