-- Create a keyspace
CREATE KEYSPACE IF NOT EXISTS books WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : '1' };

-- Create a table
CREATE TABLE IF NOT EXISTS books.Authors
(
  Id UUID PRIMARY KEY,
  FirstName text,
  LastName text,
  Address1 text,
  Address2 text,
  City text,
  State text,
  PostalCode text,
  Country text
)
;

CREATE INDEX IDX_B_Authors ON books.Authors(LastName)
;
   
-- Insert some data
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Oren','Shaffer','900-2273 Integer Avenue','','Farmer City','MD','37032','Cook Islands');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Octavia','Burke','955-7635 Natoque St.','','Kalispell','MS','66863','Cape Verde');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Aiko','Stafford','Ap #554-7132 Dictum Street','','Kankakee','ID','59675','Brunei Darussalam');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Christine','Aguilar','Ap #698-4255 Sodales Av.','','Riverton','MA','24828','Ghana');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Kenneth','Curry','P.O. Box 305, 8335 Cras St.','','Somersworth','ME','36619','Kazakhstan');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Lillian','Nieves','P.O. Box 827, 1923 Ornare Avenue','','Sierra Vista','MA','12684','Mali');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Damian','Donaldson','5047 Eu Rd.','','LaGrange','UT','64681','Thailand');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Jordan','Woodward','Ap #486-6702 Iaculis Road','','Glen Cove','OH','18011','Libyan Arab Jamahiriya');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Adele','Holmes','7585 Magna. Ave','','Villa Park','ME','00619','Cayman Islands');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Jillian','Mckee','1185 Per Ave','','Rapid City','LA','11671','New Caledonia');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Mikayla','Knight','Ap #375-6798 Auctor St.','','Sault Ste. Marie','CO','26558','Lesotho');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Denise','Dominguez','324-4875 Vitae Av.','','Hudson','SD','41933','Russian Federation');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Ira','Dunn','8869 Velit Rd.','','Lincoln','IA','74182','Zambia');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Elvis','Blevins','Ap #629-9258 In St.','','Abilene','WY','19269','Saint Vincent and The Grenadines');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Theodore','Gilliam','6293 Enim, Ave','','Baltimore','UT','86549','Saudi Arabia');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Jared','White','P.O. Box 806, 6212 Adipiscing, Road','','Thibodaux','KS','90172','China');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Melyssa','Willis','Ap #467-9359 Ipsum Rd.','','Roanoke','OH','45706','Niger');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Hilary','Marks','Ap #361-1093 Vitae Street','','Fort Lauderdale','ID','10855','Venezuela');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Frances','Ellis','Ap #458-4569 Sem. Ave','','Durham','MS','50037','Gambia');
INSERT INTO books.Authors (Id,FirstName,LastName,Address1,Address2,City,State,PostalCode,Country) VALUES (uuid(),'Lenore','Cooley','P.O. Box 937, 3415 Nulla Ave','','Half Moon Bay','OR','51967','Canada');
