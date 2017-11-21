PRAGMA foreign_keys = ON;

CREATE TABLE Deal (
  deal_source TEXT NOT NULL,
  deal_id TEXT NOT NULL,
  fo_deal_id TEXT NOT NULL,
  PRIMARY KEY (deal_source, deal_id)
);

CREATE TABLE DealCost (
  deal_source TEXT NOT NULL,
  deal_id TEXT NOT NULL,
  deal_cost_no INTEGER NOT NULL,
  fee_type TEXT NOT NULL,
  PRIMARY KEY (deal_source, deal_id, deal_cost_no),
  FOREIGN KEY (deal_source, deal_id) REFERENCES Deal (deal_source, deal_id)
);
