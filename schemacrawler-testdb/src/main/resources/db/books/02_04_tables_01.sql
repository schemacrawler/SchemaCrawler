CREATE TABLE πίνακαβάσηςδεδομένων
(
  στήληβάσηςδεδομένων BIGINT NOT NULL,
  άλληστήληβάσηςδεδομένων VARCHAR(10),  
  συγγραφέας INT,
  CONSTRAINT PK_πίνακαβάσηςδεδομένων PRIMARY KEY (στήληβάσηςδεδομένων),  
  CONSTRAINT FK_πίνακαβάσηςδεδομένων_Publishers FOREIGN KEY (συγγραφέας) REFERENCES Publishers (Id)
)
;