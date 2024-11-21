CREATE TABLE IF NOT EXISTS investment (
    investment_id SERIAL PRIMARY KEY,
    group_id INT NOT NULL,
    user_investor_id INT NOT NULL,
    account_investor_id INT NOT NULL,
    investment_amount DECIMAL(15, 2) NOT NULL,
    investment_date TIMESTAMP,
    expected_return_rate DECIMAL(5, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    settlement_ratio DECIMAL(10, 8) NOT NULL
);

CREATE TABLE IF NOT EXISTS invest_status (
    investment_id INT PRIMARY KEY,
    invest_status_type INT NOT NULL,
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
    total_invested_amount DECIMAL(15, 2) NOT NULL,
    total_return_value DECIMAL(15, 2) NOT NULL,
    total_return_rate DECIMAL(5, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS settlement (
    settlement_id SERIAL PRIMARY KEY,
    investment_id INT NOT NULL,
    settlement_date TIMESTAMP NOT NULL,
    settlement_principal DECIMAL(15, 2) NOT NULL,
    settlement_profit DECIMAL(15, 2) NOT NULL,
    FOREIGN KEY (investment_id) REFERENCES investment(investment_id)
);