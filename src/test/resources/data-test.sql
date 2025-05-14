select H2VERSION();

-- table properties
insert into properties (area,created_by,date_created,date_modified,deleted,location,modified_by,name,public_id,id) values ('Nairobi', null, now(), now(), false, 'Githurai', null, 'Property 1', 'b9c962be-b94b-42fe-8eee-c7730e5a335a', 1001);
insert into properties (area,created_by,date_created,date_modified,deleted,location,modified_by,name,public_id,id) values ('Thika', null, now(), now(), false, 'CBD', null, 'Property 2', '25e1fa0c-1dc9-11f0-9cd2-0242ac120002', 1002);

--table houses

insert into houses (created_by,date_created,date_modified,deleted,floor,modified_by,house_number,property_id,public_id,id) values (null, now(), now(), false, 'Floor 1', null, '1A', 1001, 'e5ecd302-de15-4628-97ca-f6cda684ca74', 1031);
insert into houses (created_by,date_created,date_modified,deleted,floor,modified_by,house_number,property_id,public_id,id) values (null, now(), now(), false, 'Floor 1', null, '4B', 1001, '155eeb98-1dd0-11f0-9cd2-0242ac120002', 1032);

--table persons

insert into persons (created_by,date_created,date_modified,deleted,first_name,identification_number,identification_type,last_name,modified_by,nationality,other_name,phone_number,public_id,id) values (null, now(), now(), false, 'Jutta', '543-19-1549', 'NATIONAL_ID', 'Mrs. Eddy Toy', null, 'Kenyan', 'Schiller', '0711223344', '97ebfe26-b027-4611-bd19-b2903aeeb18f', 1061);
insert into persons (created_by,date_created,date_modified,deleted,first_name,identification_number,identification_type,last_name,modified_by,nationality,other_name,phone_number,public_id,id) values (null, now(), now(), false, 'Autta', '544-19-1549', 'NATIONAL_ID', 'Mrs. Eddy Hirthe', null, 'Ugandan', 'Schiller', '0713223344', '8690ebbb-8acd-404f-afcb-d59777f5ad47', 1062);
insert into persons (created_by,date_created,date_modified,deleted,first_name,identification_number,identification_type,last_name,modified_by,nationality,other_name,phone_number,public_id,id) values (null, now(), now(), false, 'Butta', '545-19-1549', 'NATIONAL_ID', 'Mrs. Toy Hirthe', null, 'Rwandan', 'Schiller', '0714223344', '50210eb6-1de7-11f0-9cd2-0242ac120002', 1063);
insert into persons (created_by,date_created,date_modified,deleted,first_name,identification_number,identification_type,last_name,modified_by,nationality,other_name,phone_number,public_id,id) values (null, now(), now(), false, 'Cutta', '546-19-1549', 'NATIONAL_ID', 'Mrs. Hirthe', null, 'Kenyan', 'Schiller', '0715223344', '563ce202-1de7-11f0-9cd2-0242ac120002', 1064);

