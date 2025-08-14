select H2VERSION();

-- table properties
insert into properties (area,created_by,date_created,date_modified,deleted,location,modified_by,name,public_id,id) values ('Nairobi', null, now(), now(), false, 'Githurai', null, 'Property 1', 'b9c962be-b94b-42fe-8eee-c7730e5a335a', 1001);
insert into properties (area,created_by,date_created,date_modified,deleted,location,modified_by,name,public_id,id) values ('Thika', null, now(), now(), false, 'CBD', null, 'Property 2', '25e1fa0c-1dc9-11f0-9cd2-0242ac120002', 1002);
insert into properties (area,created_by,date_created,date_modified,deleted,location,modified_by,name,public_id,id) values ('Langata', null, now(), now(), false, 'Langata', null, 'Langata Property 2', '58f17d24-72cd-11f0-8de9-0242ac120002', 1003);

--table houses

insert into houses (created_by,date_created,date_modified,deleted,floor,modified_by,house_number,property_id,public_id,id) values (null, now(), now(), false, 'Floor 1', null, '1A', 1001, 'e5ecd302-de15-4628-97ca-f6cda684ca74', 1031);
insert into houses (created_by,date_created,date_modified,deleted,floor,modified_by,house_number,property_id,public_id,id) values (null, now(), now(), false, 'Floor 1', null, '4B', 1001, '155eeb98-1dd0-11f0-9cd2-0242ac120002', 1032);
insert into houses (created_by,date_created,date_modified,deleted,floor,modified_by,house_number,property_id,public_id,id) values (null, now(), now(), false, 'Floor 1', null, '1A', 1003, '9f3d7828-72cd-11f0-8de9-0242ac120002', 1033);
insert into houses (created_by,date_created,date_modified,deleted,floor,modified_by,house_number,property_id,public_id,id) values (null, now(), now(), false, 'Floor 1', null, '1B', 1003, 'a2976d3a-72cd-11f0-8de9-0242ac120002', 1034);
insert into houses (created_by,date_created,date_modified,deleted,floor,modified_by,house_number,property_id,public_id,id) values (null, now(), now(), false, 'Floor 1', null, '1C', 1003, 'a5467b0c-72cd-11f0-8de9-0242ac120002', 1035);
insert into houses (created_by,date_created,date_modified,deleted,floor,modified_by,house_number,property_id,public_id,id) values (null, now(), now(), false, 'Floor 1', null, '1D', 1003, 'a8965700-72cd-11f0-8de9-0242ac120002', 1036);
insert into houses (created_by,date_created,date_modified,deleted,floor,modified_by,house_number,property_id,public_id,id) values (null, now(), now(), false, 'Floor 2', null, '2A', 1003, 'ab42dffa-72cd-11f0-8de9-0242ac120002', 1037);
insert into houses (created_by,date_created,date_modified,deleted,floor,modified_by,house_number,property_id,public_id,id) values (null, now(), now(), false, 'Floor 2', null, '2B', 1003, '567de3ba-72ce-11f0-8de9-0242ac120002', 1038);
insert into houses (created_by,date_created,date_modified,deleted,floor,modified_by,house_number,property_id,public_id,id) values (null, now(), now(), false, 'Floor 2', null, '2C', 1003, '5aafba44-72ce-11f0-8de9-0242ac120002', 1039);
insert into houses (created_by,date_created,date_modified,deleted,floor,modified_by,house_number,property_id,public_id,id) values (null, now(), now(), false, 'Floor 2', null, '2D', 1003, '5de1a790-72ce-11f0-8de9-0242ac120002', 1040);

--table persons

insert into persons (created_by,date_created,date_modified,deleted,first_name,identification_number,identification_type,last_name,modified_by,nationality,other_name,phone_number,public_id,id) values (null, now(), now(), false, 'Jutta', '543-19-1549', 'NATIONAL_ID', 'Mrs. Eddy Toy', null, 'Kenyan', 'Schiller', '0711223344', '97ebfe26-b027-4611-bd19-b2903aeeb18f', 1061);
insert into persons (created_by,date_created,date_modified,deleted,first_name,identification_number,identification_type,last_name,modified_by,nationality,other_name,phone_number,public_id,id) values (null, now(), now(), false, 'Autta', '544-19-1549', 'NATIONAL_ID', 'Mrs. Eddy Hirthe', null, 'Ugandan', 'Schiller', '0713223344', '8690ebbb-8acd-404f-afcb-d59777f5ad47', 1062);
insert into persons (created_by,date_created,date_modified,deleted,first_name,identification_number,identification_type,last_name,modified_by,nationality,other_name,phone_number,public_id,id) values (null, now(), now(), false, 'Butta', '545-19-1549', 'NATIONAL_ID', 'Mrs. Toy Hirthe', null, 'Rwandan', 'Schiller', '0714223344', '50210eb6-1de7-11f0-9cd2-0242ac120002', 1063);
insert into persons (created_by,date_created,date_modified,deleted,first_name,identification_number,identification_type,last_name,modified_by,nationality,other_name,phone_number,public_id,id) values (null, now(), now(), false, 'Cutta', '546-19-1549', 'NATIONAL_ID', 'Mrs. Hirthe', null, 'Kenyan', 'Schiller', '0715223344', '563ce202-1de7-11f0-9cd2-0242ac120002', 1064);
insert into persons (created_by,date_created,date_modified,deleted,first_name,identification_number,identification_type,last_name,modified_by,nationality,other_name,phone_number,public_id,id) values (null, now(), now(), false, 'JuttB', '543-19-1549', 'NATIONAL_ID', 'Mrs. Bddy Toy', null, 'Kenyan', 'Schiller', '0711223344', '5979d3c0-72cf-11f0-8de9-0242ac120002', 1065);
insert into persons (created_by,date_created,date_modified,deleted,first_name,identification_number,identification_type,last_name,modified_by,nationality,other_name,phone_number,public_id,id) values (null, now(), now(), false, 'JuttC', '543-19-1549', 'NATIONAL_ID', 'Mrs. Cddy Toy', null, 'Kenyan', 'Schiller', '0711223344', '5ef95294-72cf-11f0-8de9-0242ac120002', 1066);

insert into rentals (amount,created_by,date_created,date_modified,deleted,house_id,modified_by,person_id,public_id,status,id) values (2000, null, now(), now(), false, 1034, null, 1065, '0deccb69-3904-4687-8006-53525d82b4a3', 'Occupied', 1090);